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
	"strings"
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

func mergeFiles(s3Client *s3.S3, request LambdaRequest) (string, int, error) {
	var mergedContent strings.Builder
	var nextContinuationToken *string
	fileCount := 0

	for {
		params := &s3.ListObjectsV2Input{
			Bucket:            aws.String(request.InputBucket),
			Prefix:            aws.String(request.InputDirectory),
			ContinuationToken: nextContinuationToken,
		}
		result, err := s3Client.ListObjectsV2(params)
		if err != nil {
			return "", fileCount, &MergeFilesError{"Failed to list anonymization scripts", err}
		}

		for _, content := range result.Contents {
			if strings.HasSuffix(*content.Key, ".sql") {
				obj, err := s3Client.GetObject(&s3.GetObjectInput{
					Bucket: aws.String(request.InputBucket),
					Key:    content.Key,
				})
				if err != nil {
					return "", fileCount, &MergeFilesError{fmt.Sprintf("Failed to get anonymization script %s", *content.Key), err}
				}

				buf := new(strings.Builder)
				_, err = io.Copy(buf, obj.Body)
				if err != nil {
					return "", fileCount, &MergeFilesError{fmt.Sprintf("Failed to read anonymization script %s", *content.Key), err}
				}
				mergedContent.WriteString(buf.String())
				mergedContent.WriteString("\n")

				fileCount++
				fmt.Printf("Appended anonymization script: %s\n", *content.Key)
			}
		}

		if *result.IsTruncated {
			nextContinuationToken = result.NextContinuationToken
		} else {
			break
		}
	}

	return mergedContent.String(), fileCount, nil
}

func handler(ctx context.Context, request LambdaRequest) (string, error) {
	sess := session.Must(session.NewSession())
	s3Client := s3.New(sess)
	mergedContent, fileCount, err := mergeFiles(s3Client, request)
	if err != nil {
		fmt.Println(err)
		return "", errors.New("Failed to merge anonymization scripts")
	}

	outputKey := fmt.Sprintf("%s/%s", request.InputDirectory, request.OutputScript)
	_, err = s3Client.PutObject(&s3.PutObjectInput{
		Bucket: aws.String(request.OutputBucket),
		Key:    aws.String(outputKey),
		Body:   strings.NewReader(mergedContent),
	})
	if err != nil {
		fmt.Printf("Failed to write merged anonymization script to %s: %v\n", outputKey, err)
		return "", errors.New("Failed to write merged anonymization script")
	}

	return fmt.Sprintf("Merged %d anonymization files, and written the output to bucket %s, location: %s", fileCount, request.OutputBucket, outputKey),
		nil
}

func main() {
	lambda.Start(handler)
}
