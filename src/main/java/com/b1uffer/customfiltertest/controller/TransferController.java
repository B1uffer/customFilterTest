package com.b1uffer.customfiltertest.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TransferController {

    @GetMapping("/transfer")
    public String transferForm(Model model, CsrfToken csrfToken) {
        model.addAttribute("_csrf", csrfToken);
        return "transfer";
    }

    @PostMapping("/transfer")
    @ResponseBody
    public String doTransfer(@RequestParam int amount) {
        return "송금 완료" + amount + "원";
    }
}
