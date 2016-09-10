## Required request headers
To load / send representations of resources use 'Content-Type' / 'Accept' headers.

## Optional request parameters
To simulate service delay use request parameter 'delay' with integer value in seconds.

To log entities saving or updating operation use request parameter 'logging' with 'true' or 'false' value.
Service logs information in AuthorLogs and BookLogs tables in database.

## Authentication
Service uses Basic Authentication for user authorization.
You can use request header or request parameter with name 'Authorization'. 

User with name 'user' is allowed to use next url: /authors/, /books/ ;

User with name 'admin' is allowed to use next url: /books/;

### Author
/authors
- GET - loads all authors (returns HTTP_STATUS.OK and requested resources)
- POST - saves new author (returns HTTP_STATUS.OK and created resource)
- DELETE - deletes all authors (returns HTTP_STATUS.NO_CONTENT)

/authors/{id}
- GET - loads author by id (returns HTTP_STATUS.OK and requested resources)
- POST - updates author by id (returns HTTP_STATUS.OK and updated resources)
- DELETE - deletes the author (returns HTTP_STATUS.NO_CONTENT)

/authors/{id}/books
- GET - returns books for the author id (returns HTTP_STATUS.OK and requested resources)

### Book
/books
- GET - loads all books (returns HTTP_STATUS.OK and requested resources)
- POST - saves new book (returns HTTP_STATUS.OK and created resource)
- DELETE - deletes all books (returns HTTP_STATUS.NO_CONTENT)

/books/{id}
- GET - loads book by id (returns HTTP_STATUS.OK and requested resources)
- POST - updates book by id (returns HTTP_STATUS.NO_CONTENT)
- DELETE - deletes the book (returns HTTP_STATUS.NO_CONTENT)
