package com.learning.hotelManagementSystem.DTO.NotificationDTO;

import java.util.List;

public record EmailDTO(List<String> to, String subject, String body, boolean isHtmlMessage) {
}
