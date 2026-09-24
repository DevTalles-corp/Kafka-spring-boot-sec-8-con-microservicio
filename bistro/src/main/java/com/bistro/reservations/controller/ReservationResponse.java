package com.bistro.reservations.controller;

import com.bistro.reservations.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private String reservationCode;
    private ReservationStatus status;
    private Long assignedTableId;
}
