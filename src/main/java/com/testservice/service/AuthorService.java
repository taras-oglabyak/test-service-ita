package com.testservice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.testservice.domain.Author;

/**
 * AuthorService is the service for CRUD operation on {@link Author} instance in the database.
 *
 */
@Component
public class AuthorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(AuthorService.class);

    /**
     * Loads all {@link Author} instances from the database.
     * 
     * @return List of Author instances
     * @throws DataAccessException
     */
    public List<Author> loadAll() {
        try {
            return jdbcTemplate.query("select * from Author", new BeanPropertyRowMapper<Author>(Author.class));
        } catch (DataAccessException e) {
            LOGGER.error("Unable to load authors", e);
            throw e;
        }
    }

    /**
     * Loads {@link Author} instance from database by its identifier.
     * 
     * @param id identifier of Author instance
     * @return Author instance
     */
    public Author load(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from Author where id=?", new Object[] { id },
                    new BeanPropertyRowMapper<Author>(Author.class));
        } catch (DataAccessException e) {
            LOGGER.error("Unable to load author with id=" + id, e);
            throw e;
        }
    }

    /**
     * Deletes all authors from database.
     * @return Author instance
     * @throws DataAccessException
     */
    public void deleteAll() {
        try {
            jdbcTemplate.update("delete from Author");
        } catch (DataAccessException e) {
            LOGGER.error("Unable to delete authors", e);
            throw e;
        }
    }

    /**
     * Deletes {@link Author} instance from the database.
     * 
     * @param id identifier of Author instance should be deleted
     * @throws DataAccessException
     */
    public void delete(int id) {
        try {
            jdbcTemplate.update("delete from Author where id=?", new Object[] { id });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to delete author with id=" + id, e);
            throw e;
        }
    }

    /**
     * Saves {@link Author} instance to database.
     * 
     * @param author Author instance should be saved in the database
     * @return Author instance
     * @throws DataAccessException
     */
    public Author save(Author author) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement("insert into Author values (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, 0);
                    ps.setString(2, author.getFirstName());
                    ps.setString(3, author.getLastName());
                    ps.setInt(4, author.getAge());
                    ps.setDouble(5, author.getSalary());
                    return ps;
                }
            }, keyHolder);
            author.setId(keyHolder.getKey().intValue());
            return author;
        } catch (DataAccessException e) {
            LOGGER.error("Unable to save author with lastName=" + author.getLastName(), e);
            throw e;
        }
    }

    /**
     * Updates {@link Author} instances in the database.
     * 
     * @param author Author instance should be updated
     * @throws DataAccessException
     */
    public void update(Author author) {
        try {
            jdbcTemplate.update("update Author set firstName=?, lastName=?, age=?, salary=? where id=?", new Object[] {
                    author.getFirstName(), author.getLastName(), author.getAge(), author.getSalary(), author.getId() });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to update author with id=" + author.getId(), e);
            throw e;
        }
    }

    /**
     * Saves logs about author's change.
     * 
     * @param author Author instance which should be updated
     */
    public void saveLogs(Author author) {
        try {
            jdbcTemplate.update("insert into AuthorLogs values (?, ?, ?, ?)",
                    new Object[] { null, author.getId(), author.getFirstName(), author.getLastName() });
        } catch (DataAccessException e) {
            LOGGER.error("Unable to save author's logs, author id=" + author.getId(), e);
            throw e;
        }
    }
}