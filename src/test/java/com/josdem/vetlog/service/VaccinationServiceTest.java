/*
Copyright 2024 Jose Morales contact@josdem.io

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.josdem.vetlog.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.josdem.vetlog.enums.PetType;
import com.josdem.vetlog.enums.VaccinationStatus;
import com.josdem.vetlog.exception.BusinessException;
import com.josdem.vetlog.model.Breed;
import com.josdem.vetlog.model.Pet;
import com.josdem.vetlog.model.Vaccination;
import com.josdem.vetlog.repository.VaccinationRepository;
import com.josdem.vetlog.service.impl.VaccinationServiceImpl;
import com.josdem.vetlog.strategy.vaccination.VaccinationStrategy;
import com.josdem.vetlog.strategy.vaccination.impl.CatVaccinationStrategy;
import com.josdem.vetlog.strategy.vaccination.impl.DogVaccinationStrategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
class VaccinationServiceTest {

    private VaccinationService vaccinationService;

    @Mock
    private VaccinationRepository vaccinationRepository;

    private DogVaccinationStrategy dogVaccinationStrategy;

    private CatVaccinationStrategy catVaccinationStrategy;

    private Map<PetType, VaccinationStrategy> vaccinationStrategies;

    private final Pet pet = new Pet();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        dogVaccinationStrategy = new DogVaccinationStrategy(vaccinationRepository);
        catVaccinationStrategy = new CatVaccinationStrategy(vaccinationRepository);

        vaccinationStrategies = new HashMap<>();
        vaccinationStrategies.put(PetType.DOG, dogVaccinationStrategy);
        vaccinationStrategies.put(PetType.CAT, catVaccinationStrategy);

        vaccinationService = new VaccinationServiceImpl(vaccinationRepository, vaccinationStrategies);
        pet.setBreed(new Breed());
    }

    @Test
    @DisplayName("not saving a pet if it is not a dog or cat")
    void shouldNotSavePetIfItIsNotADogOrCat(TestInfo testInfo) {
        log.info("Test: {}", testInfo.getDisplayName());
        pet.getBreed().setType(PetType.BIRD);
        assertThrows(BusinessException.class, () -> vaccinationService.save(pet));
    }

    @DisplayName("saving vaccines")
    @ParameterizedTest
    @CsvSource({"6, 2", "10, 3", "14, 4", "20, 5"})
    void shouldSaveVaccines(int weeks, int times) {
        log.info("Test: saving vaccines");
        pet.getBreed().setType(PetType.DOG);
        pet.setBirthDate(LocalDateTime.now().minusWeeks(weeks));
        vaccinationService.save(pet);
        verify(vaccinationRepository, times(times))
                .save(new Vaccination(null, any(), LocalDate.now(), VaccinationStatus.PENDING, pet));
    }

    @Test
    @DisplayName("not saving vaccination due is not old enough")
    void shouldNotSaveVaccinationDueToNotOldEnough(TestInfo testInfo) {
        log.info("Test: {}", testInfo.getDisplayName());
        pet.getBreed().setType(PetType.DOG);
        pet.setBirthDate(LocalDateTime.now().minusWeeks(1));
        vaccinationService.save(pet);
        verify(vaccinationRepository, never())
                .save(new Vaccination(null, any(), LocalDate.now(), VaccinationStatus.PENDING, pet));
    }
}