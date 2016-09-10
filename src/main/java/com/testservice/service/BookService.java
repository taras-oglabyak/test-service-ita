package com.testservice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.testservice.domain.Book;

/**
 * BookService is the service for CRUD operation on {@link Book} instance in the database.
 *
 */
@Component
public class BookService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(BookService.class);

    /**
     * Loads all {@link Book} instances from the database.
     * 
     * @return List of Book instances
     * @throws DataAccessException
     */
    public List<Book> loadAll() {
        try {
            return jdbcTemplate.query("select * from Book", new BeanPropertyRowMapper<Book>(Book.class));
        } catch (DataAccessException e) {
            LOGGER.error("Unable to load books", e);
            throw e;
        }
    }

    /**
     * Loads {@link Book} instance from database by its identifier.
     * 
     * @param id identifier of Book instance
     * @return Book instance
     */
    public Book load(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from Book where id=?", new Object[] { id },
                    new BeanPropertyRowMapper<Book>(Book.class));
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Unable to load book with id=" + id, e);
            throw e;
        }
    }

    /**
     * Deletes all books from database.
     * 
     * @throws DataAccessException
     */
    public void deleteAll() {
        try {
            jdbcTemplate.update("delete from Book");
        } catch (DataAccessException e) {
            LOGGER.error("Unable to delete books", e);
            throw e;
        }
    }

    /**
     * Deletes {@link Book} instance from the database.
     * 
     * @param id identifier of Book instance should be deleted
     * @throws DataAccessException
     */
    public void delete(int id) {
        try {
            jdbcTemplate.update("delete from Book where id=?", new Object[] { id });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to delete book with id=" + id, e);
            throw e;
        }
    }

    /**
     * Saves {@link Book} instance to database.
     * 
     * @param book Book instance should be saved in the database
     * @throws DataAccessException
     */
    public Book save(Book book) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement("insert into Book values (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, 0);
                    ps.setString(2, book.getName());
                    ps.setInt(3, book.getYear());
                    ps.setInt(4, book.getAuthorId());
                    return ps;
                }
            }, keyHolder);
            book.setId(keyHolder.getKey().intValue());
            return book;
        } catch (DataAccessException e) {
            LOGGER.error("Unable to save book with name=" + book.getName(), e);
            throw e;
        }
    }

    /**
     * Updates {@link Book} instances in the database.
     * 
     * @param book Book instance should be updated
     * @throws DataAccessException
     */
    public void update(Book book) {
        try {
            jdbcTemplate.update("update Book set name=?, year=?, authorId=? where id=?",
                    new Object[] { book.getName(), book.getYear(), book.getAuthorId(), book.getId() });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to update book with id=" + book.getId(), e);
            throw e;
        }
    }

    /**
     * Saves logs about book's change.
     * 
     * @param book Book instance which should be updated
     */
    public void saveLogs(Book book) {
        try {
            jdbcTemplate.update("insert into BookLogs values (?, ?, ?)",
                    new Object[] { null, book.getId(), book.getName() });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to save book's logs, author id=" + book.getId(), e);
            throw e;
        }
    }

    /**
     * Loads all {@link Book} instances for the Author from the database.
     * 
     * @param id identifier of Author which Books should be loaded
     * @return List of Book instances
     * @throws DataAccessException
     */
    public List<Book> getBooksByAuthor(int id) {
        try {
            return jdbcTemplate.query("select * from Book where authorId=?",
                    new BeanPropertyRowMapper<Book>(Book.class), new Object[] { id });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to load book's for author with id=" + id, e);
            throw e;
        }
    }
}