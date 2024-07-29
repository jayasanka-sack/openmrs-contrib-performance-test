package org.openmrs.performance.registries;

import io.gatling.javaapi.core.ChainBuilder;
import org.openmrs.performance.http.HttpService;

import java.util.Set;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static org.openmrs.performance.Constants.*;

public abstract class Registry<T extends HttpService> {
	public T httpService;
	
	public Registry(T httpService) {
		this.httpService = httpService;
	}
	
	public ChainBuilder login(){
		return exec(
				httpService.loginRequest(),
				pause(1),
				httpService.getLocations(),
				pause(5),
				httpService.selectLocation());
	}
	
	public ChainBuilder openHomePage() {
		return exec(
				httpService.getAddressTemplate(),
				httpService.getRelationshipTypes(),
				httpService.getAppointmentsForSpecificDate("2024-05-15T00:00:00.000+0530"),
				httpService.getModuleInformation(),
				httpService.getPatientIdentifierTypes(),
				httpService.getPrimaryIdentifierTermMapping(),
				httpService.getVisitsOfLocation(OUTPATIENT_CLINIC_LOCATION_UUID),
				httpService.getAutoGenerationOptions(),
				httpService.getIdentifierSources(ID_CARD_SOURCE_UUID),
				httpService.getIdentifierSources(OPENMRS_ID_SOURCE_UUID),
				httpService.getIdentifierSources(UNKNOWN_TYPE_SOURCE_UUID),
				httpService.getIdentifierSources(LEGACY_ID_SOURCE_UUID),
				httpService.getIdentifierSources(SSN_SOURCE_UUID),
				httpService.getIdentifierSources(UNKNOWN_TYPE_2_SOURCE_UUID)
		);
	}
	
	public ChainBuilder openPatientChartPage(String patientUuid){
		Set<String> unknownObservationSet = Set.of(
				SYSTOLIC_BLOOD_PRESSURE,
				DIASTOLIC_BLOOD_PRESSURE,
				PULSE,
				TEMPERATURE_C,
				ARTERIAL_BLOOD_OXYGEN_SATURATION,
				HEIGHT_CM,
				WEIGHT_KG,
				RESPIRATORY_RATE,
				UNKNOWN_OBSERVATION_TYPE,
				MID_UPPER_ARM_CIRCUMFERENCE
		);
		
		Set<String> vitals = Set.of(
				SYSTOLIC_BLOOD_PRESSURE,
				DIASTOLIC_BLOOD_PRESSURE,
				PULSE,
				TEMPERATURE_C,
				ARTERIAL_BLOOD_OXYGEN_SATURATION,
				RESPIRATORY_RATE,
				UNKNOWN_OBSERVATION_TYPE
		);
		
		Set<String> biometrics = Set.of(
				HEIGHT_CM,
				WEIGHT_KG,
				MID_UPPER_ARM_CIRCUMFERENCE
		);
		return exec(
				httpService.getPatientSummaryData(patientUuid),
				httpService.getCurrentVisit(patientUuid),
				httpService.getPatientObservations(patientUuid, unknownObservationSet),
				httpService.getPatientObservations(patientUuid, vitals),
				httpService.getPatientObservations(patientUuid, biometrics),
				httpService.getVisitQueueEntry(patientUuid),
				httpService.getPatientConditions(patientUuid),
				httpService.getActiveOrders(patientUuid));
	}
}
