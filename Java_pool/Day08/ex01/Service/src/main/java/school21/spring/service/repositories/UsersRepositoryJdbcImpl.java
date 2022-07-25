package school21.spring.service.repositories;

import school21.spring.service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersRepositoryJdbcImpl implements UsersRepository {
    private DataSource dataSource;

    public UsersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public User findById(Long id) {
        String queryStr = "SELECT * FROM users WHERE id = ?;";

        try (Connection con = dataSource.getConnection();
             PreparedStatement query = con.prepareStatement(queryStr);) {
            query.setLong(1, id);
           try (ResultSet r = query.executeQuery();) {
               if (r.next()) {
                   return new User(r.getLong(1), r.getString(2));
               }
           }
        } catch (SQLException e) {}
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> retList = new ArrayList<>();
        String queryStr = "SELECT * FROM users;";

        try (Connection con = dataSource.getConnection();
             PreparedStatement query = con.prepareStatement(queryStr);
             ResultSet r = query.executeQuery();) {
            while (r.next()) {
                User u = new User(r.getLong(1), r.getString(2));
                retList.add(u);
            }
        } catch (SQLException e) {}
        return retList;
    }

    @Override
    public void save(User entity) {
        String queryStr = "INSERT INTO users VALUES(?, ?);";

        try (Connection con = dataSource.getConnection();
             PreparedStatement query = con.prepareStatement(queryStr);) {
            query.setLong(1, entity.getId());
            query.setString(2, entity.getEmail());
            query.executeUpdate();
        } catch (SQLException e) {}
    }

    @Override
    public void update(User entity) {
        String queryStr = "UPDATE users SET email = ? WHERE id = ?;";

        try (Connection con = dataSource.getConnection();
             PreparedStatement query = con.prepareStatement(queryStr);) {
            query.setString(1, entity.getEmail());
            query.setLong(2, entity.getId());
            query.executeUpdate();
        } catch (SQLException e) {}
    }

    @Override
    public void delete(Long id) {
        String queryStr = "DELETE FROM users WHERE id = ?;";

        try (Connection con = dataSource.getConnection();
             PreparedStatement query = con.prepareStatement(queryStr);) {
            query.setLong(1, id);
            query.executeUpdate();
        } catch (SQLException e) {}
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String queryStr = "SELECT * FROM users WHERE email = ?;";

        try (Connection con = dataSource.getConnection();
             PreparedStatement query = con.prepareStatement(queryStr);) {
            query.setString(1, email);
            try (ResultSet r = query.executeQuery();) {
                if (r.next()) {
                    return Optional.of(new User(r.getLong(1), r.getString(2)));
                }
            }
        } catch (SQLException e) {}
        return Optional.empty();
    }
}
