package com.work.controller;

import com.work.entity.Phone;
import com.work.entity.User;
import com.work.repository.PhoneRepository;
import com.work.repository.UserRepository;
import com.work.service.Verify;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by yuuto on 3/10/17.
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PhoneController {

    @Autowired
    private Verify verify;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String homePage(Model model, Authentication authentication){
        if(authentication == null){
            return "login";
        }
        String userLogin = authentication.getName();
        User user = userRepository.findByLogin(userLogin).get();
        List<Phone> phones = phoneRepository.findByUser(user);
        model.addAttribute("phones",phones);
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/search")
    public String search(Model model,
                         @RequestParam(value = "search") String searchString,
                         Authentication authentication){
        if(authentication == null){
            return "login";
        }
        String userLogin = authentication.getName();
        User user = userRepository.findByLogin(userLogin).get();
        List<Phone> phones = phoneRepository.searchByExpr(user,searchString);
        model.addAttribute("phones",phones);
        return "index";
    }

    @RequestMapping(value = "/createPhone", method = RequestMethod.GET)
    public String createPhone(Model model){
        return "phone";
    }

    @RequestMapping(value = "/createPhoneHandler", method = RequestMethod.POST)
    public String createPhoneHandler(@RequestParam(value = "name") String name,
                                  @RequestParam(value = "surname") String surname,
                                  @RequestParam(value = "patronymic") String patronymic,
                                  @RequestParam(value = "mobile") String mobile,
                                  @RequestParam(value = "home",required = false) String phone,
                                  @RequestParam(value = "address", required = false) String address,
                                  @RequestParam(value = "email", required = false) String email,
                                  Authentication authentication,
                                  Model model){

        if(!verify.isValidName(name)){
            model.addAttribute("errorName", "Invalid name");
            return homePage(model, authentication);
        }
        if(!verify.isValidName(surname)){
            model.addAttribute("errorSurname", "Invalid surname");
            return homePage(model, authentication);
        }
        if(!verify.isValidName(patronymic)){
            model.addAttribute("errorPatronymic", "Invalid patronymic");
            return homePage(model, authentication);
        }
        if(!verify.isValidPhone(mobile)){
            model.addAttribute("errorMobile", "Invalid format mobile phone. Format: +380(66)1234567");
            return homePage(model, authentication);
        }
        if(!phone.isEmpty() && !verify.isValidPhone(phone)){
            model.addAttribute("errorHome", "Invalid format home phone. Format: +380(66)1234567");
            return homePage(model, authentication);
        }
        if(!email.isEmpty() && !verify.isValidEmail(email)){
            model.addAttribute("errorEmail", "Invalid format email.");
            return homePage(model, authentication);
        }

        Phone entityPhone = new Phone();
        entityPhone.setName(name);
        entityPhone.setSurname(surname);
        entityPhone.setPatronymic(patronymic);
        entityPhone.setMobilePhone(mobile);
        entityPhone.setHomePhone(phone);
        entityPhone.setAddress(address);
        entityPhone.setEmail(email);
        String userLogin = authentication.getName();
        User user = userRepository.findByLogin(userLogin).get();
        entityPhone.setUser(user);
        phoneRepository.save(entityPhone);
        return homePage(model, authentication);
    }

    @RequestMapping(value = "/editPhone/{id}", method = RequestMethod.GET)
    public String editPhone(@PathVariable String id, Model model, Authentication authentication) {
        Phone entityPhone = phoneRepository.findOne(Long.parseLong(id));
        if(!entityPhone.getUser().getLogin().equals(authentication.getName())){
            model.addAttribute("errorMessage", "You dont have permission");
            return "error";
        }
        model.addAttribute("phone",entityPhone);
        return "editPhone";
    }
    @RequestMapping(value = "/removePhone/{id}", method = RequestMethod.GET)
    public String removePhone(@PathVariable String id, Model model, Authentication authentication) {
        Phone entityPhone = phoneRepository.findOne(Long.parseLong(id));
        if(!entityPhone.getUser().getLogin().equals(authentication.getName())){
            model.addAttribute("errorMessage", "You dont have permission");
            return "error";
        }
        phoneRepository.delete(entityPhone);
        return homePage(model, authentication);
    }

    @RequestMapping(value = "/editPhoneHandler/{id}", method = RequestMethod.POST)
    public String editPhoneHandler(@RequestParam(value = "name") String name,
                                     @RequestParam(value = "surname") String surname,
                                     @RequestParam(value = "patronymic") String patronymic,
                                     @RequestParam(value = "mobile") String mobile,
                                     @RequestParam(value = "home",required = false) String phone,
                                     @RequestParam(value = "address", required = false) String address,
                                     @RequestParam(value = "email", required = false) String email,
                                     @PathVariable String id,
                                     Authentication authentication,
                                     Model model){

        if(!verify.isValidName(name)){
            model.addAttribute("errorName", "Invalid name");
            return editPhone(id,model,authentication);
        }
        if(!verify.isValidName(surname)){
            model.addAttribute("errorSurname", "Invalid surname");
            return editPhone(id,model,authentication);
        }
        if(!verify.isValidName(patronymic)){
            model.addAttribute("errorPatronymic", "Invalid patronymic");
            return editPhone(id,model,authentication);
        }
        if(!verify.isValidPhone(mobile)){
            model.addAttribute("errorMobile", "Invalid format mobile phone. Format: +380(66)1234567");
            return editPhone(id,model,authentication);
        }
        if(!phone.isEmpty() && !verify.isValidPhone(phone)){
            model.addAttribute("errorHome", "Invalid format home phone. Format: +380(66)1234567");
            return editPhone(id,model,authentication);
        }
        if(!email.isEmpty() && !verify.isValidEmail(email)){
            model.addAttribute("errorEmail", "Invalid format email.");
            return editPhone(id,model,authentication);
        }

        Phone entityPhone = phoneRepository.findOne(Long.parseLong(id));
        entityPhone.setName(name);
        entityPhone.setSurname(surname);
        entityPhone.setPatronymic(patronymic);
        entityPhone.setMobilePhone(mobile);
        entityPhone.setHomePhone(phone);
        entityPhone.setAddress(address);
        entityPhone.setEmail(email);
        String userLogin = authentication.getName();
        User user = userRepository.findByLogin(userLogin).get();
        entityPhone.setUser(user);
        phoneRepository.save(entityPhone);
        return homePage(model, authentication);
    }
}
