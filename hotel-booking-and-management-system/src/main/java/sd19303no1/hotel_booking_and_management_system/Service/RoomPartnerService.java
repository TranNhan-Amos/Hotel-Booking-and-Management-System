package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Repository.RoomPartnerRepository;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;

@Service
public class RoomPartnerService {

    @Autowired
    private RoomPartnerRepository roomPartnerRepository;
   
    public List<RoomPartnerEntity> findByPartnerId(Long  partnerId) {
        return roomPartnerRepository.findByPartnerId(partnerId);
    }

    public RoomPartnerEntity save(RoomPartnerEntity roomPartner) {
    return roomPartnerRepository.save(roomPartner);
}
    
}
