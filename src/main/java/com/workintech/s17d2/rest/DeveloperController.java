package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private Taxable taxable;



    @PostConstruct
    public void init(){
        developers=new HashMap<>();

        developers.put(1,new Developer(1,"Ali",50000,Experience.JUNIOR));
        developers.put(2,new Developer(2,"Doğancan",80000,Experience.SENIOR));
    }



    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }



    @GetMapping("/developers")
    public List<Developer> getDevelopers(){
        return developers.values().stream().toList();
    }

    @GetMapping("/developers/{id}")
    public Developer getDeveloper(@PathVariable int id){
        return developers.get(id);
    }


    @PostMapping("/developers")
    @ResponseStatus(HttpStatus.CREATED)
    public Developer save(@RequestBody Developer developer){
        if(developer.getExperience().equals(Experience.JUNIOR)){
            double salary=developer.getSalary()-(developer.getSalary()* taxable.getSimpleTaxRate()/100);
            developers.put(developer.getId(),new JuniorDeveloper(developer.getId(),developer.getName(),salary));
        } else if (developer.getExperience().equals(Experience.MID)) {
            double salary=developer.getSalary()-(developer.getSalary()* taxable.getMiddleTaxRate()/100);
            developers.put(developer.getId(),new MidDeveloper(developer.getId(),developer.getName(),salary));
        }else if (developer.getExperience().equals(Experience.SENIOR)) {
            double salary = developer.getSalary() - (developer.getSalary()* taxable.getUpperTaxRate()/100);
            developers.put(developer.getId(), new SeniorDeveloper(developer.getId(), developer.getName(), salary));
        }
        return developer;
    }

    @PutMapping("/developers/{id}")
    public Developer update(@PathVariable int id,@RequestBody Developer developer){
        developers.put(id,new Developer(developer.getId(),developer.getName(),developer.getSalary(),developer.getExperience()));
        return developer;
    }

    @DeleteMapping("/developers/{id}")
    public Developer delete(@PathVariable int id){
        Developer developer=developers.get(id);
        developers.remove(id,developer);
        return developer;
    }
}