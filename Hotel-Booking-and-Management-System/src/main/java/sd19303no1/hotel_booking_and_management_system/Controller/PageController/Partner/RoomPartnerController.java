package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Partner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

@Controller
public class RoomPartnerController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;



    @GetMapping("/partner/rooms")
    public String viewPartnerRooms(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    Long partnerId = partner.getId();
                    List<RoomEntity> roomPartners = roomService.findByPartnerId(partnerId);

                    for (RoomEntity roomPartner : roomPartners) {
                        if (roomPartner.getAmenities() == null) {
                            roomPartner.setAmenities(new ArrayList<>());
                        }
                    }

                    long availableRoomsCount = roomPartners.stream()
                            .filter(room -> room.getStatus() == RoomEntity.RoomStatus.AVAILABLE)
                            .count();
                    long unavailableRoomsCount = roomPartners.stream()
                            .filter(room -> room.getStatus() != RoomEntity.RoomStatus.AVAILABLE)
                            .count();

                    model.addAttribute("roomPartners", roomPartners);
                    model.addAttribute("partner", partner);
                    model.addAttribute("availableRoomsCount", availableRoomsCount);
                    model.addAttribute("unavailableRoomsCount", unavailableRoomsCount);
                    return "Partner/RoomPartner";
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "Partner/RoomPartner";
                }
            } else {
                model.addAttribute("error", "Bạn không có quyền truy cập.");
                return "Partner/RoomPartner";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "Partner/RoomPartner";
        }
    }

    @GetMapping("/partner/rooms/add")
    public String showAddRoomForm(Model model) {
        return "redirect:/partner/rooms?action=add";
    }

    @GetMapping("/partner/rooms/edit/{id}")
    public String showEditRoomForm(@PathVariable("id") Integer id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            PartnerEntity partner = partnerService.findBySystemUser(systemUser);

            if (partner == null) {
                model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                return "redirect:/partner/rooms";
            }

            RoomEntity room = roomService.getRoomById(id);
            if (room == null) {
                model.addAttribute("error", "Không tìm thấy phòng.");
                return "redirect:/partner/rooms";
            }

            if (room.getPartner() == null || !room.getPartner().getId().equals(partner.getId())) {
                model.addAttribute("error", "Bạn không có quyền chỉnh sửa phòng này.");
                return "redirect:/partner/rooms";
            }

            // Lấy danh sách ảnh từ database (đã có sẵn trong room object)
            if (room.getImageUrls() == null) {
                room.setImageUrls(new ArrayList<>());
            }

            // Debug logging
            System.out.println("Room ID: " + room.getRoomId());
            System.out.println("Room Name: " + room.getNameNumber());
            System.out.println("Image URLs: " + room.getImageUrls());
            System.out.println("Image URLs Size: " + (room.getImageUrls() != null ? room.getImageUrls().size() : 0));

            model.addAttribute("room", room);
            return "Partner/edit-RoomPartner";

        } catch (Exception e) {
            System.err.println("Error in showEditRoomForm: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/partner/rooms";
        }
    }

    @PostMapping("/partner/rooms/add")
    public String addRoom(@ModelAttribute("roomPartner") @Valid RoomEntity roomPartner,
                         BindingResult result,
                         @RequestParam(value = "images", required = false) MultipartFile[] images,
                         RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ.");
                return "redirect:/partner/rooms";
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            PartnerEntity partner = partnerService.findBySystemUser(systemUser);

            if (partner == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đối tác.");
                return "redirect:/partner/rooms";
            }

            roomPartner.setPartner(partner);
            roomService.saveRoom(roomPartner);

            // Upload ảnh nếu có
            if (images != null && images.length > 0) {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/rooms";
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs();
                }
                
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        try {
                            String originalFilename = image.getOriginalFilename();
                            String newFilename = System.currentTimeMillis() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
                            
                            File destFile = new File(uploadDirectory, newFilename);
                            image.transferTo(destFile);
                            
                            imageUrls.add(newFilename);
                            
                        } catch (IOException e) {
                            System.err.println("Error saving image " + image.getOriginalFilename() + ": " + e.getMessage());
                            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu ảnh " + image.getOriginalFilename());
                        }
                    }
                }
                
                roomPartner.setImageUrls(imageUrls);
            }

            redirectAttributes.addFlashAttribute("success", "Thêm phòng thành công!");
            return "redirect:/partner/rooms";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/partner/rooms";
        }
    }

    @PostMapping("/partner/rooms/edit")
    public String updateRoom(@ModelAttribute RoomEntity roomPartner,
                           @RequestParam(value = "roomImages", required = false) MultipartFile[] roomImages,
                           @RequestParam(value = "deletedImages", required = false) String deletedImages,
                           @RequestParam(value = "amenities", required = false) String[] amenities,
                           RedirectAttributes redirectAttributes) {
        System.out.println("=== UPDATE ROOM REQUEST ===");
        System.out.println("Room ID: " + roomPartner.getRoomId());
        System.out.println("deletedImages: " + deletedImages);
        System.out.println("roomImages count: " + (roomImages != null ? roomImages.length : 0));
        System.out.println("amenities: " + (amenities != null ? Arrays.toString(amenities) : "null"));
        
        try {
            RoomEntity existingRoom = roomService.getRoomById(roomPartner.getRoomId());

            if (existingRoom == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng.");
                return "redirect:/partner/rooms";
            }

            // Cập nhật thông tin phòng
            existingRoom.setRoomNumber(roomPartner.getRoomNumber());
            existingRoom.setNameNumber(roomPartner.getNameNumber());
            existingRoom.setType(roomPartner.getType());
            existingRoom.setPrice(roomPartner.getPrice());
            existingRoom.setStatus(roomPartner.getStatus());
            existingRoom.setDescription(roomPartner.getDescription());
            existingRoom.setService(roomPartner.getService());
            existingRoom.setCapacity(roomPartner.getCapacity());
            existingRoom.setTotalRooms(roomPartner.getTotalRooms());
            existingRoom.setBedType(roomPartner.getBedType());
            existingRoom.setBathroomType(roomPartner.getBathroomType());
            existingRoom.setView(roomPartner.getView());
            existingRoom.setIsSmoking(roomPartner.getIsSmoking());
            existingRoom.setIsPetFriendly(roomPartner.getIsPetFriendly());

            if (amenities != null) {
                existingRoom.setAmenities(new ArrayList<>(Arrays.asList(amenities)));
            } else {
                existingRoom.setAmenities(new ArrayList<>());
            }

            // Xử lý ảnh bị xóa
            System.out.println("=== PROCESSING DELETED IMAGES ===");
            System.out.println("deletedImages parameter: " + deletedImages);
            
            if (deletedImages != null && !deletedImages.isEmpty()) {
                String[] deletedImageUrls = deletedImages.split(",");
                System.out.println("Split deleted images: " + Arrays.toString(deletedImageUrls));
                
                List<String> currentImages = existingRoom.getImageUrls();
                if (currentImages == null) {
                    currentImages = new ArrayList<>();
                }
                System.out.println("Current images before deletion: " + currentImages);
                
                for (String imageUrl : deletedImageUrls) {
                    if (!imageUrl.trim().isEmpty()) {
                        System.out.println("Processing deletion for: " + imageUrl.trim());
                        
                        // Xóa file từ filesystem
                        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/rooms";
                        File imageFile = new File(uploadDir, imageUrl.trim());
                        System.out.println("Attempting to delete file: " + imageFile.getAbsolutePath());
                        
                        if (imageFile.exists()) {
                            if (imageFile.delete()) {
                                System.out.println("Successfully deleted file: " + imageFile.getAbsolutePath());
                            } else {
                                System.err.println("Failed to delete file: " + imageFile.getAbsolutePath());
                            }
                        } else {
                            System.out.println("File not found: " + imageFile.getAbsolutePath());
                        }
                        
                        // Xóa khỏi danh sách ảnh
                        boolean removed = currentImages.removeIf(img -> img.equals(imageUrl.trim()));
                        System.out.println("Removed from list: " + removed);
                        System.out.println("Image to remove: '" + imageUrl.trim() + "'");
                        System.out.println("Current images after removal attempt: " + currentImages);
                    }
                }
                
                System.out.println("Current images after deletion: " + currentImages);
                existingRoom.setImageUrls(currentImages);
            } else {
                System.out.println("No images to delete");
            }

            // Upload ảnh mới
            if (roomImages != null && roomImages.length > 0) {
                System.out.println("=== UPLOADING NEW IMAGES ===");
                System.out.println("Room ID: " + existingRoom.getRoomId());
                System.out.println("Images count: " + roomImages.length);
                
                // Xử lý upload trực tiếp thay vì gọi API
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/rooms";
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs();
                }
                
                List<String> newImageUrls = new ArrayList<>();
                for (MultipartFile image : roomImages) {
                    if (!image.isEmpty()) {
                        try {
                            String originalFilename = image.getOriginalFilename();
                            String newFilename = System.currentTimeMillis() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
                            
                            System.out.println("Processing image: " + originalFilename + " -> " + newFilename);
                            
                            File destFile = new File(uploadDirectory, newFilename);
                            image.transferTo(destFile);
                            
                            newImageUrls.add(newFilename);
                            System.out.println("Image saved: " + destFile.getAbsolutePath());
                            
                        } catch (IOException e) {
                            System.err.println("Error saving image " + image.getOriginalFilename() + ": " + e.getMessage());
                            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu ảnh " + image.getOriginalFilename());
                        }
                    }
                }
                
                // Cập nhật danh sách ảnh trong database
                if (!newImageUrls.isEmpty()) {
                    List<String> currentImages = existingRoom.getImageUrls();
                    if (currentImages == null) {
                        currentImages = new ArrayList<>();
                    }
                    currentImages.addAll(newImageUrls);
                    existingRoom.setImageUrls(currentImages);
                    System.out.println("Updated room images: " + currentImages);
                }
            }

            existingRoom.setUpdatedAt(LocalDateTime.now());
            roomService.saveRoom(existingRoom);

            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng thành công!");
            return "redirect:/partner/rooms";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/partner/rooms";
        }
    }

    @GetMapping("/partner/rooms/delete/{id}")
    public String deleteRoom(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            PartnerEntity partner = partnerService.findBySystemUser(systemUser);

            RoomEntity room = roomService.getRoomById(id);
            if (room == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng.");
                return "redirect:/partner/rooms";
            }

            if (room.getPartner() == null || !room.getPartner().getId().equals(partner.getId())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa phòng này.");
                return "redirect:/partner/rooms";
            }

            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa phòng: " + e.getMessage());
        }
        return "redirect:/partner/rooms";
    }
}