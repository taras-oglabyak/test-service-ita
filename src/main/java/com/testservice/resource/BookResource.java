package com.testservice.resource;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.testservice.domain.Book;
import com.testservice.service.BookService;

/**
 * BookResource handles requests, which URL starts with '/books'.
 * 
 * @author taras
 *
 */
@Path("/books")
@RolesAllowed({ "user", "admin" })
@Component
public class BookResource extends GeneralResource {

    @Autowired
    private BookService bookService;

    /**
     * Retrieves all books.
     * 
     * @return {@link Response} entity with Books List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getBooks() {
        List<Book> books = bookService.loadAll();
        GenericEntity<List<Book>> entity = new GenericEntity<List<Book>>(books) {};
        return ok(entity);
    }

    /**
     * Handles saving new book.
     * 
     * @param book {@link Book} instance should be saved
     * @return {@link Response} entity with saved {@link Book} instance
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response saveBook(Book book) {
        book = bookService.save(book);
        if (logging) {
            bookService.saveLogs(book);
        }
        return ok(book);
    }

    /**
     * Handles deleting books.
     * 
     * @return if success returns HTTP_STATUS 204
     */
    @DELETE
    public Response deleteBooks() {
        bookService.deleteAll();
        return NO_CONTENT;
    }

    /**
     * Retrieves {@link Book} instance with the identifier.
     * 
     * @param id identifier of {@link Book} instance should be retrieved
     * @return {@link Response} entity with requested Book instance
     */
    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getBook(@PathParam("id") int id) {
        Book book = null;
        try {
            book = bookService.load(id);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if (book == null) {
            return NOT_FOUND;
        }
        return ok(book);
    }

    /**
     * Updates {@link Book} instance with the identifier.
     * 
     * @param book {@link Book} instance should be updated
     * @param id identifier of {@link Book} instance should be retrieved
     * @return if success returns HTTP_STATUS 204.
     */
    @POST
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateBook(Book book, @PathParam("id") int id) {
        book.setId(id);
        bookService.update(book);
        if (logging) {
            bookService.saveLogs(book);
        }
        return NO_CONTENT;
    }

    /**
     * Handles deleting book.
     * 
     * @param id identifier of {@link Book} instance should be deleted
     * @return if success returns HTTP_STATUS 204.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") int id) {
        bookService.delete(id);
        return NO_CONTENT;
    }
}