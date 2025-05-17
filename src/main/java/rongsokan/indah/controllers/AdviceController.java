package rongsokan.indah.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import rongsokan.indah.services.PageService;

@ControllerAdvice
public class AdviceController {

    @Autowired
    private PageService pageService;

    @ModelAttribute
    public void getDataLogin(Model model) {
        pageService.getDataLogin(model);
    }
}