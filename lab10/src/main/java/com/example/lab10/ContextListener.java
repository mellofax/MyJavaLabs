package com.example.lab10;

import com.example.lab10.db.UserDAO;
import com.example.lab10.db.YachtClubDAO;
import com.example.lab10.model.YachtClub;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    private UserDAO userDAO;
    private YachtClubDAO yachtClubDAO;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;user=user;password=max");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        userDAO = new UserDAO(connection);
        yachtClubDAO = new YachtClubDAO(connection);

        final ServletContext servletContext = servletContextEvent.getServletContext();

        servletContext.setAttribute("userDAO", userDAO);
        servletContext.setAttribute("yachtClubDAO", yachtClubDAO);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            userDAO.closeConnection();
            yachtClubDAO.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
