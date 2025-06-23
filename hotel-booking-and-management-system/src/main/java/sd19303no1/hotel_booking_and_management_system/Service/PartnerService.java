package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.PartnerRepository;

@Service
public class PartnerService {
    
    @Autowired
    private PartnerRepository partnerRepository;
    
    public PartnerEntity findBySystemUser(SystemUserEntity systemUser) {
        return partnerRepository.findBySystemUser(systemUser);
    }
    
    public PartnerEntity findByEmail(String email) {
        return partnerRepository.findByEmail(email);
    }
    
    public PartnerEntity save(PartnerEntity partner) {
        return partnerRepository.save(partner);
    }

  
}
