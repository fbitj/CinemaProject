package com.stylefeng.guns.rest.modular.pic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("pic")
public class PictureController {

    public void pic(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/pic");
        request.getRequestDispatcher("localhost/pic" + split[split.length - 1])
                .forward(request, response);
        //response.sendRedirect("localhost/pic" + split[split.length - 1]);
    }
}
