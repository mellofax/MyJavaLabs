package com.example.lab9.filter;

import com.example.lab9.db.UserDAO;
import com.example.lab9.model.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

import static java.util.Objects.nonNull;

public class AuthFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        filterConfig = fConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;

        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String hashPassword = null;
        if(nonNull(password)) {
            hashPassword = DigestUtils.md5Hex(password);
        }


        UserDAO userDAO = (UserDAO) req.getServletContext().getAttribute("userDAO");

        HttpSession session = req.getSession();
        if(nonNull(session) &&
                nonNull(session.getAttribute("login")) &&
                nonNull(session.getAttribute("password"))) {
            User.ROLE role = (User.ROLE) session.getAttribute("role");

            //setCookie
            setCookie(req, res);

            moveToMenu(req, res, role);
        } else {
            String finalHashPassword = hashPassword;
            User user = userDAO.getAll()
                    .stream()
                    .filter(u -> u.getLogin().equals(login) &&
                            u.getPassword().equals(finalHashPassword))
                    .findFirst().orElse(null);
            if (user != null) {
                User.ROLE role = user.getRole();

                req.getSession().setAttribute("login", login);
                req.getSession().setAttribute("password", hashPassword);
                req.getSession().setAttribute("role", role);

                //setCookie
                setCookie(req, res);

                moveToMenu(req, res, role);
            } else {
                if(nonNull(login) && nonNull(password)) {
                    req.setAttribute("info", "???????????? ???????????????? ?????????? ?????? ????????????");
                }
                moveToMenu(req, res, User.ROLE.UNKNOWN);
            }
        }
    }

    private void setCookie(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Cookie cookie = Arrays.stream(req.getCookies()).filter(c -> c.getName().equals("qtyVisit")).findFirst().orElse(null);
        int qtyVisit;
        if (cookie == null) {
            qtyVisit = 0;
        } else {
            qtyVisit = Integer.parseInt(cookie.getValue());
        }
        res.addCookie(new Cookie("qtyVisit", String.valueOf(qtyVisit + 1)));
    }

    private void moveToMenu(HttpServletRequest req, HttpServletResponse res, User.ROLE role) throws ServletException, IOException {
        switch (role) {
            case ADMIN:
                req.getRequestDispatcher("auth/admin").forward(req, res);
                break;
            case USER:
                req.getRequestDispatcher("auth/user").forward(req, res);
                break;
            default:
                filterConfig.getServletContext().getRequestDispatcher("/auth/login").forward(req, res);
                break;
        }
    }

    @Override
    public void destroy()
    {
        filterConfig = null;
    }
}
