package com.example.cap.Service;

import com.example.cap.Model.SurplusItems;
import com.example.cap.Model.SurplusOrders;
import com.example.cap.Model.User;
import com.example.cap.Repository.SurplusItemsRepository;
import com.example.cap.Repository.SurplusOrdersRepository;
import com.example.cap.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SurplusOrdersService {
    private final SurplusOrdersRepository surplusOrdersRepository;
    private final SurplusItemsRepository surplusItemsRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public String add(SurplusOrders surplusOrders) {

        SurplusItems surplusItems = surplusItemsRepository.findById(surplusOrders.getSurplusItemId()).orElse(null);
        if (surplusItems == null) {
            return "Surplus item not found";
        }

        User user=userRepository.findUsersById(surplusOrders.getBuyerId());
        if(user==null){
            return "Buyer not found";
        }

        if(user.getRole().equals("ADMIN")){
            return "Admin users cannot buy surplus items";
        }

        if (surplusItems.getSellerId().equals(surplusOrders.getBuyerId())) {
            return "You cannot buy your own surplus";
        }

        if (!surplusItems.getStatus().equals("AVAILABLE")) {
            return "Surplus item is not available";
        }
        if (surplusOrders.getQuantity() > surplusItems.getQuantity()) {
            return "Quantity exceeds available surplus";
        }
        surplusOrders.setPaid(false);
        surplusOrdersRepository.save(surplusOrders);

        return "Surplus order created successfully";
    }


      public String pay(Integer buyerId, Integer surplusOrderId){

       SurplusOrders surplusOrders=surplusOrdersRepository.findById(surplusOrderId).orElse(null);
        if(surplusOrders==null){
            return "Surplus order not found";
        }

        if (!surplusOrders.getBuyerId().equals(buyerId)) {
              return "You do not own this order";
        }

        if (surplusOrders.getPaid()) {
              return "Order is already paid";
        }


        SurplusItems surplusItems = surplusItemsRepository.findById(surplusOrders.getSurplusItemId()).orElse(null);
          if (surplusItems == null) {
              return "Surplus item not found";
          }
          if (surplusItems.getQuantity() < surplusOrders.getQuantity()) {
              return "Quantity exceeds available surplus";
          }

          surplusItems.setQuantity(surplusItems.getQuantity() - surplusOrders.getQuantity());

          if (surplusItems.getQuantity() == 0) {
              surplusItems.setStatus("SOLD");
           }

          surplusOrders.setPaid(true);

          surplusItemsRepository.save(surplusItems);
          surplusOrdersRepository.save(surplusOrders);

          // رسالة للبائع
          User seller = userRepository.findUsersById(surplusItems.getSellerId());

          emailService.sendEmail(
                  seller.getEmail(),
                  "Surplus Purchased",
                  "Your surplus item has been purchased successfully.\n\n"
                          + "Quantity purchased: " + surplusOrders.getQuantity() + "\n"
                          + "Remaining quantity: " + surplusItems.getQuantity() + "\n\n"
                          + "Please cooperate with the buyer and arrange the delivery or pickup of the purchased materials.\n"
                          + "As a seller, you are responsible for providing the materials according to the purchase details.\n\n"
                          + "The buyer has completed the purchase and has the right to receive the purchased quantity.\n"
                          + "If you face any issue or dispute, please contact BAYTI support.\n\n"
                          + "BAYTI is committed to providing a trusted marketplace and supporting the rights of both buyers and sellers."
          );

          // رسالة للمشتري
          User buyer = userRepository.findUsersById(surplusOrders.getBuyerId());

          emailService.sendEmail(
                  buyer.getEmail(),
                  "Surplus Purchase Confirmed",
                  "Your purchase has been completed successfully.\n\n"
                          + "Quantity purchased: " + surplusOrders.getQuantity() + "\n"
                          + "Seller contact number: " + seller.getPhone() + "\n\n"
                          + "Please contact the seller to arrange the pickup of your materials.\n\n"
                          + "If you face any issue with your purchase, please contact BAYTI support.\n\n"
                          + "BAYTI is committed to providing a trusted platform and helping protect the rights of both buyers and sellers."
          );

          return "Surplus order paid successfully";
      }


      }








