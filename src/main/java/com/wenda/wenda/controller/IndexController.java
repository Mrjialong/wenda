package com.wenda.wenda.controller;

import com.wenda.wenda.service.WendaServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {
    @Autowired
    WendaServive service;



    @RequestMapping(path = {"/followees"})
    public String templateFooter(Model model){
        return "followees";
    }

    @RequestMapping(path = {"/header"})
    public String templateHeader(Model model){
        return "header";
    }

    @RequestMapping(path = {"/test"})
    public String templateTest(Model model){
        return "feeds";
    }

    @RequestMapping(path = {"/index2"})
    public String index(Model model){
        model.addAttribute("model","modelaaaaaaaaaaa");
        return "index2.ftl";
    }


}
