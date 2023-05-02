package main

import (
	"context"
	"fmt"
	"io"
	"strings"

	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

type LambdaRequest struct {
	Bucket string `json:"bucket"`
	Prefix string `json:"prefix"`
}

func mergeFiles(s3Client *s3.S3, bucket, prefix string) (string, error) {
	params := &s3.ListObjectsV2Input{
		Bucket: aws.String(bucket),
		Prefix: aws.String(prefix),
	}
	result, err := s3Client.ListObjectsV2(params)
	if err != nil {
		return "", err
	}

	var mergedContent strings.Builder

	for _, content := range result.Contents {
		if strings.HasSuffix(*content.Key, ".sql") {
			obj, err := s3Client.GetObject(&s3.GetObjectInput{
				Bucket: aws.String(bucket),
				Key:    content.Key,
			})
			if err != nil {
				return "", err
			}

			buf := new(strings.Builder)
			_, err = io.Copy(buf, obj.Body)
			if err != nil {
				return "", err
			}
			mergedContent.WriteString(buf.String())
			mergedContent.WriteString("\n")
		}
	}

	return mergedContent.String(), nil
}

func handler(ctx context.Context, request LambdaRequest) (string, error) {
	sess := session.Must(session.NewSession())
	s3Client := s3.New(sess)

	mergedContent, err := mergeFiles(s3Client, request.Bucket, request.Prefix)
	if err != nil {
		return "", err
	}

	outputKey := fmt.Sprintf("%s/merged_output.sql", request.Prefix)
	_, err = s3Client.PutObject(&s3.PutObjectInput{
		Bucket: aws.String(request.Bucket),
		Key:    aws.String(outputKey),
		Body:   strings.NewReader(mergedContent),
	})
	if err != nil {
		return "", err
	}

	return fmt.Sprintf("Merged files written to %s", outputKey), nil
}

func main() {
	lambda.Start(handler)
}
