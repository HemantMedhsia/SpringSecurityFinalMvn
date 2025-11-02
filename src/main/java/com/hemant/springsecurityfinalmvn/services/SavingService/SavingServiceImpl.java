package com.hemant.springsecurityfinalmvn.services.SavingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hemant.springsecurityfinalmvn.dtos.common.ApiResponse;
import com.hemant.springsecurityfinalmvn.dtos.saving.AddSavingDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.SavingResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.SavingsModel;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.SavingRepo;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

	private final UserRepo userRepo;
	private final SavingRepo savingRepo;

	// ------------------- PRIVATE HELPERS -------------------

	private UserModel getCurrentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	private SavingResponseDto mapToDto(SavingsModel s) {
		return new SavingResponseDto(
				s.getId(),
				s.getCategory(),
				s.getSource(),
				s.getSavedAmount(),
				s.getIcon(),
				s.getFileUrl(),
				s.getDate(),
				new SavingResponseDto.OwnerInfo(
						s.getOwner().getId().toString(),
						s.getOwner().getName()
				)
		);
	}

	// ------------------- CREATE -------------------

	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> createSaving(AddSavingDto savingDto) {

		UserModel currentUser = getCurrentUser();

		SavingsModel saving = SavingsModel.builder()
				.category(savingDto.category())
				.source(savingDto.source())
				.savedAmount(savingDto.savedAmount())
				.icon(savingDto.icon())
				.fileUrl(savingDto.fileUrl())
				.date(savingDto.date() != null ? savingDto.date() : LocalDate.now())
				.owner(currentUser)
				.build();

		SavingsModel savedSaving = savingRepo.save(saving);

		SavingResponseDto responseDto = new SavingResponseDto(
				savedSaving.getId(),
				savedSaving.getCategory(),
				savedSaving.getSource(),
				savedSaving.getSavedAmount(),
				savedSaving.getIcon(),
				savedSaving.getFileUrl(),
				savedSaving.getDate(),
				new SavingResponseDto.OwnerInfo(
						currentUser.getId().toString(),
						currentUser.getName()
				)
		);

		return ApiResponse.success(responseDto, "Saving created successfully", HttpStatus.CREATED);
	}

	// ------------------- READ -------------------

	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> getSavingById(Long id) {
		UserModel currentUser = getCurrentUser();

		SavingsModel saving = savingRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Saving not found with id " + id));

		if (!saving.getOwner().getId().equals(currentUser.getId())) {
			throw new AccessDeniedException("You are not authorized to view this saving");
		}

		SavingResponseDto responseDto = mapToDto(saving);
		return ApiResponse.success(responseDto, "Saving fetched successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SavingResponseDto>>> getAllSaving() {
		UserModel currentUser = getCurrentUser();

		List<SavingsModel> saving = savingRepo.findByOwnerId(currentUser.getId());

		List<SavingResponseDto> savingDto = saving.stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());

		return ApiResponse.success(savingDto, "All savings fetched successfully", HttpStatus.OK);
	}

	// ------------------- DELETE -------------------

	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> deleteSaving(Long id) {
		UserModel currentUser = getCurrentUser();

		SavingsModel saving = savingRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Saving not found with id: " + id));

		if (!saving.getOwner().getId().equals(currentUser.getId())) {
			throw new AccessDeniedException("You are not authorized to delete this saving");
		}

		savingRepo.delete(saving);
		return ApiResponse.success(null, "Saving deleted successfully", HttpStatus.OK);
	}

	// ------------------- UPDATE -------------------

	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> updateSaving(Long id, AddSavingDto updatedSavingDto) {
		UserModel currentUser = getCurrentUser();

		SavingsModel saving = savingRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Saving not found with id: " + id));

		if (!saving.getOwner().getId().equals(currentUser.getId())) {
			throw new AccessDeniedException("You are not authorized to modify this saving");
		}

		if (updatedSavingDto.category() != null) {
			saving.setCategory(updatedSavingDto.category());
		}
		if (updatedSavingDto.savedAmount() != null) {
			saving.setSavedAmount(updatedSavingDto.savedAmount());
		}
		if (updatedSavingDto.source() != null) {
			saving.setSource(updatedSavingDto.source());
		}
		if (updatedSavingDto.date() != null) {
			saving.setDate(updatedSavingDto.date());
		}
		if (updatedSavingDto.fileUrl() != null) {
			saving.setFileUrl(updatedSavingDto.fileUrl());
		}
		if (updatedSavingDto.icon() != null) {
			saving.setIcon(updatedSavingDto.icon());
		}

		SavingsModel updated = savingRepo.save(saving);
		SavingResponseDto responseDto = mapToDto(updated);

		return ApiResponse.success(responseDto, "Saving updated successfully", HttpStatus.OK);
	}

	// ------------------- CALCULATIONS -------------------

	@Override
	public ResponseEntity<ResponseStructure<Double>> getTotalSaving() {
		UserModel currentUser = getCurrentUser();

		List<SavingsModel> savings = savingRepo.findByOwnerId(currentUser.getId());

		double totalSaved = savings.stream()
				.mapToDouble(SavingsModel::getSavedAmount)
				.sum();

		return ApiResponse.success(totalSaved, "Total savings calculated successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<Map<String, Double>>> getSavingByMonth() {
		UserModel currentUser = getCurrentUser();

		List<SavingsModel> savings = savingRepo.findByOwnerId(currentUser.getId());

		Map<String, Double> savingsByMonth = savings.stream()
				.filter(s -> s.getDate() != null)
				.collect(Collectors.groupingBy(
						s -> s.getDate().getMonth().toString(),
						Collectors.summingDouble(SavingsModel::getSavedAmount)
				));

		return ApiResponse.success(savingsByMonth, "Monthly savings calculated successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<Double>> getSavingSumByMonth(String month) {
		UserModel currentUser = getCurrentUser();

		List<SavingsModel> savings = savingRepo.findByOwnerId(currentUser.getId());

		double total = savings.stream()
				.filter(s -> s.getDate() != null)
				.filter(s -> s.getDate().getMonth().toString().equalsIgnoreCase(month))
				.mapToDouble(SavingsModel::getSavedAmount)
				.sum();

		String message = String.format("Total savings for %s fetched successfully", month.toUpperCase());
		return ApiResponse.success(total, message, HttpStatus.OK);
	}
}
