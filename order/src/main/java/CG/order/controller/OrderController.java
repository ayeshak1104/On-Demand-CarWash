package CG.order.controller;

import CG.order.exceptionHandlers.API_requestException;
import CG.order.model.OrderDetails;
import CG.order.repository.OrderRepo;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepo or;

    //To get all orders
    @GetMapping("/findall")
    public List<OrderDetails> findallOrders(){
        return or.findAll();
    }
    //Find one object by ID
    @GetMapping("/findone/{id}")
    public ResponseEntity<OrderDetails> findoneOrder(@PathVariable int id){
         OrderDetails order=or.findById(id).orElseThrow(()-> new API_requestException("Order with ID -> "+id+" not found"));
         return ResponseEntity.ok(order);
    }
    //To add an order
    @PostMapping("/add")
    public OrderDetails addOrder(@RequestBody OrderDetails order) {
        //Every Order at conception will be [Pending] and [Unassigned]
        order.setStatus("Pending");
        order.setWasherName("NA");
        return or.save(order);
    }
    //To delete specific order with id
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String,Boolean>> deleteOrder(@PathVariable int orderId){
        OrderDetails order=or.findById(orderId).orElseThrow(()-> new API_requestException("Order with ID -> "+orderId+" not found,deletion failed"));
        or.delete(order);
        Map<String, Boolean> reponse = new HashMap<>();
        reponse.put("Order Deleted", Boolean.TRUE);
        return ResponseEntity.ok(reponse);
    }
    //To update an order
    @PutMapping("/update/{orderId}")
    public ResponseEntity<OrderDetails> updateOrder(@PathVariable int orderId,@RequestBody OrderDetails orderDetails){
        OrderDetails existingOrder=or.findById(orderId).orElseThrow(() -> new API_requestException("Order with ID -> "+orderId+" not found,update failed"));
        existingOrder.setWasherName(orderDetails.getWasherName());
        existingOrder.setWashpackId(orderDetails.getWashpackId());
        //Status can't be updated by the user
        existingOrder.setCars(orderDetails.getCars());
        existingOrder.setPhoneNo(orderDetails.getPhoneNo());
        OrderDetails order=or.save(existingOrder);
        return ResponseEntity.ok(order);
    }

    /** Getting consumed by the Washer and Admin model */
    //To find all the completed orders
    @GetMapping("/findCompleted")
    public List<OrderDetails> getCompletedOrders(){
         return or.findAll().stream().filter(x -> x.getStatus().contains("Completed")).collect(Collectors.toList());
    }
    //To find all the pending orders
    @GetMapping("/findPending")
    public List<OrderDetails> getPendingOrders(){
        return or.findAll().stream().filter(x -> x.getStatus().contains("Pending")).collect(Collectors.toList());
    }
    //To find all the cancelled orders
    @GetMapping("/findCancelled")
    public List<OrderDetails> getCancelledOrders(){
        return or.findAll().stream().filter(x -> x.getStatus().contains("Cancelled")).collect(Collectors.toList());
    }
    //To find all the unassigned orders
    @GetMapping("/findUnassigned")
    public List<OrderDetails> getUnassignedOrders(){
        return or.findAll().stream().filter(x -> x.getWasherName().contains("NA")).collect(Collectors.toList());
    }
    //To cancel the order
    @PutMapping("/cancelOrder")
    public String cancelOrder(@RequestBody OrderDetails orderDetails){
        OrderDetails od=or.findById(orderDetails.getOrderId()).get();
        od.setStatus("Cancelled");
        or.save(od);
        return "The order with ID -> "+orderDetails.getOrderId()+" is cancelled successfully";
    }

    /** Methods that are consumed exclusively by rest templates below this comment */
    //This is called by Admin to update the status of the order
    @PutMapping("/updateStatus")
    public OrderDetails updateStatus(@RequestBody OrderDetails orderDetails){
        boolean doesOrderExists=or.existsById(orderDetails.getOrderId());
        if (doesOrderExists){
            OrderDetails existingOrder = or.findById(orderDetails.getOrderId()).orElse(null);
            existingOrder.setStatus(orderDetails.getStatus());
            return or.save(existingOrder);
        }
        else {
            throw new API_requestException("Order not found in database, status not updated");
        }
    }
    @PutMapping("/assignWasher")
    public OrderDetails assignWasher(@RequestBody OrderDetails od){
        boolean doesOrderExists=or.existsById(od.getOrderId());
        OrderDetails existingOrder = or.findById(od.getOrderId()).orElse(null);
        if (doesOrderExists && existingOrder.getWasherName().contains("NA")){
            existingOrder.setWasherName(od.getWasherName());
            return or.save(existingOrder);
        }
        else {
            throw new API_requestException("Order not found in database, washer not assigned");
        }
    }
}
