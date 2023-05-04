package main

import (
	"context"
	"database/sql"
	"fmt"
	"github.com/aws/aws-lambda-go/lambda"
	_ "github.com/lib/pq"
)

type Response struct {
	Message string `json:"message"`
}

func HandleRequest(ctx context.Context) (Response, error) {
	fmt.Println("Inside handle request")
	connStr := fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=disable",
		"localhost", 5433, "your_db_user", "your_db_password", "db-1eddaad9-5e40-4f0f-96d6-782dede5aa22-20230430113219")

	db, err := sql.Open("postgres", connStr)
	if err != nil {
		fmt.Println("Error opening the database:", err)
		return Response{Message: "Error opening the database"}, err
	}
	defer db.Close()

	var salary string
	err = db.QueryRow("SELECT salary FROM employees LIMIT 1").Scan(&salary)
	if err != nil {
		fmt.Println("Error querying the database:", err)
		return Response{Message: "Error querying the database"}, err
	}

	fmt.Printf("Retrieved salary: %s\n", salary)

	return Response{Message: fmt.Sprintf("Retrieved salary: %s", salary)}, nil
}

func main() {
	lambda.Start(HandleRequest)
}
