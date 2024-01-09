package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"io"
	"log"
	"strings"
	"sync"
)

type LambdaRequest struct {
	InputBucket    string `json:"inputBucket"`
	InputDirectory string `json:"inputDirectory"`
	OutputBucket   string `json:"outputBucket"`
	OutputScript   string `json:"outputScript"`
}

type MergeFilesError struct {
	Message string
	Err     error
}

func (e *MergeFilesError) Error() string {
	return fmt.Sprintf("%s: %v", e.Message, e.Err)
}

func (e *MergeFilesError) Unwrap() error {
	return e.Err
}

func listFiles(s3Client *s3.S3, request LambdaRequest) ([]*string, error) {
	files := make([]*string, 0)
	var nextContinuationToken *string

	for {
		params := &s3.ListObjectsV2Input{
			Bucket:            aws.String(request.InputBucket),
			Prefix:            aws.String(request.InputDirectory),
			ContinuationToken: nextContinuationToken,
		}

		result, err := s3Client.ListObjectsV2(params)
		if err != nil {
			return files, &MergeFilesError{"Failed to list anonymization scripts", err}
		}

		for _, content := range result.Contents {
			if strings.HasSuffix(*content.Key, ".sql") {
				files = append(files, content.Key)
			}
		}

		if *result.IsTruncated {
			nextContinuationToken = result.NextContinuationToken
		} else {
			break
		}
	}

	return files, nil
}

func processFile(s3Client *s3.S3, bucket, key string) (string, error) {
	obj, err := s3Client.GetObject(&s3.GetObjectInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(key),
	})

	if err != nil {
		return "", err
	}

	buf := new(strings.Builder)
	_, err = io.Copy(buf, obj.Body)
	if err != nil {
		return "", err
	}

	return buf.String(), nil
}

func mergeFiles(s3Client *s3.S3, request LambdaRequest) (string, int, error) {
	files, err := listFiles(s3Client, request)
	if err != nil {
		return "", 0, err
	}

	fileCount := len(files)
	fileContentChan := make(chan string, fileCount)
	errorChan := make(chan error, fileCount)
	var wg sync.WaitGroup
	wg.Add(fileCount)

	concurrencyLimit := 10
	semaphore := make(chan struct{}, concurrencyLimit)

	for _, fileKey := range files {
		go func(key string) {
			semaphore <- struct{}{}
			defer wg.Done()
			defer func() { <-semaphore }()

			content, err := processFile(s3Client, request.InputBucket, key)
			if err != nil {
				log.Printf("Failed to process anonymization script %s: %v", key, err)
				errorChan <- err
				return
			}
			fileContentChan <- content
			log.Printf("Appended anonymization script: %s\n", key)
		}(*fileKey)
	}

	wg.Wait()
	close(fileContentChan)
	close(errorChan)

	if len(errorChan) > 0 {
		return "", 0, errors.New("One or more errors occurred while processing files")
	}

	var mergedContent strings.Builder
	for content := range fileContentChan {
		mergedContent.WriteString(content)
		mergedContent.WriteString("\n")
	}

	return mergedContent.String(), fileCount, nil
}

func handler(ctx context.Context, request LambdaRequest) (string, error) {
	sess := session.Must(session.NewSession())
	s3Client := s3.New(sess)
	mergedContent, fileCount, err := mergeFiles(s3Client, request)

	if err != nil {
		log.Println(err)
		return "", errors.New("Failed to merge anonymization scripts")
	}

	outputKey := fmt.Sprintf("%s/%s", request.InputDirectory, request.OutputScript)
	_, err = s3Client.PutObject(&s3.PutObjectInput{
		Bucket: aws.String(request.OutputBucket),
		Key:    aws.String(outputKey),
		Body:   strings.NewReader(mergedContent),
	})

	if err != nil {
		log.Printf("Failed to write merged anonymization script to %s: %v\n", outputKey, err)
		return "", errors.New("Failed to write merged anonymization script")
	}

	return fmt.Sprintf("Merged %d anonymization files, and written the output to bucket %s, location: %s", fileCount, request.OutputBucket, outputKey),
		nil
}

func main() {
	lambda.Start(handler)
}
