package CG.admin.controller;

import CG.admin.model.*;
import CG.admin.WrapperModel.WasherRatings;
import CG.admin.service.AdminService;
import CG.admin.service.WashPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController{
    @Autowired
    AdminService as;
    @Autowired
    WashPackService wps;

    /** Washer controls through admin using service object */
    //To find all the washpack
    @GetMapping("/findallWP")
    public List<WashPacks> findallWP(){
        return wps.findallWP();
    }
    //To find one WashPack with ID
    @GetMapping("/findoneWP/{id}")
    public WashPacks findoneWP(@PathVariable int id){
        return wps.findoneWP(id);
    }
    //To add a new WashPack
    @PostMapping("/addWP")
    public WashPacks addWP(@RequestBody WashPacks washPacks){
        return wps.addWP(washPacks);
    }
    //To delete a Washpack
    @DeleteMapping("/deleteWP/{id}")
    public String deleteWP(@PathVariable int id){
        return wps.deleteWP(id);
    }
    //To update a Washpack
    @PutMapping("/updateWP")
    public WashPacks updateWP(@RequestBody WashPacks washPacks){
        return wps.updateWP(washPacks);
    }

    /** Order controls through admin using rest template */
    //To assign a washer to the order by Admin
    @PutMapping("/assignWasher")
    public OrderDetails assignWasher(@RequestBody OrderDetails orderDetails){
        return as.assignWasher(orderDetails);
    }

    /** Washer controls through admin using rest template */

    //To get one washer
    @GetMapping("/oneWasher/{name}")
    public User getOneWasher(@PathVariable String name){
        return as.getOneWasher(name);
    }
    //To get all the ratings of a specific Washer
    @GetMapping("/washerRating/{name}")
    public WasherRatings washerSpecificRatings(@PathVariable String name){
        return as.washerSpecificRatings(name);
    }
}

