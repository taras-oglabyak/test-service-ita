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
import org.springframework.stereotype.Component;

import com.testservice.domain.Author;
import com.testservice.domain.Book;
import com.testservice.service.AuthorService;
import com.testservice.service.BookService;

/**
 * AuthorResource handles requests, which URL starts with '/authors'.
 * 
 * @author taras
 *
 */
@Path("/authors/")
@RolesAllowed("user")
@Component
public class AuthorResource extends GeneralResource {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    /**
     * Retrieves all authors.
     * 
     * @return {@link Response} entity with Authors List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAuthors() {
        List<Author> authors = authorService.loadAll();
        GenericEntity<List<Author>> entity = new GenericEntity<List<Author>>(authors) { };
        return ok(entity);
    }

    /**
     * Handles saving new author.
     * 
     * @param author {@link Author} instance should be saved
     * @return {@link Response} entity with saved {@link Author} instance
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response saveAuthor(Author author) {
        author = authorService.save(author);
        if (logging) {
            authorService.saveLogs(author);
        }
        return ok(author);
    }

    /**
     * Handles deleting authors.
     * 
     * @return if success returns HTTP_STATUS 204
     */
    @DELETE
    public Response deleteAuthors() {
        authorService.deleteAll();
        return NO_CONTENT;
    }

    /**
     * Retrieves {@link Author} instance with the identifier.
     * 
     * @param id identifier of {@link Author} instance should be retrieved
     * @return {@link Response} entity with requested Author instance
     */
    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAuthor(@PathParam("id") int id) {
        Author author = null;
        try {
            author = authorService.load(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (author == null) {
            return NOT_FOUND;
        }
        return ok(author);
    }

    /**
     * Updates {@link Author} instance with the identifier.
     * 
     * @param author {@link Author} instance should be updated
     * @param id identifier of {@link Author} instance should be retrieved
     * @return if success returns HTTP_STATUS 204.
     */
    @POST
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateAuthor(Author author, @PathParam("id") int id) {
        author.setId(id);
        authorService.update(author);
        if (logging) {
            authorService.saveLogs(author);
        }
        return ok(author);
    }

    /**
     * Handles deleting author.
     * 
     * @param id identifier of {@link Author} instance should be deleted
     * @return if success returns HTTP_STATUS 204.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") int id) {
        authorService.delete(id);
        return NO_CONTENT;
    }

    /**
     * Retrieves all books for the author.
     * 
     * @param id identifier of {@link Author} instance which books should be retrieved
     * @return {@link Response} entity with Books List.
     */
    @GET
    @Path("/{id}/books")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getBooksByAuthor(@PathParam("id") int id) {
        List<Book> books = bookService.getBooksByAuthor(id);
        GenericEntity<List<Book>> entity = new GenericEntity<List<Book>>(books) { };
        return ok(entity);
    }
}