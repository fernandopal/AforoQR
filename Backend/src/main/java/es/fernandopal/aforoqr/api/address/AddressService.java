package es.fernandopal.aforoqr.api.address;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.appuser.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AppUserRepository appUserRepository;

    public Optional<Address> getRoom(Long id) {
        return addressRepository.findAddressById(id);
    }
    public List<Address> getAll() {
        return addressRepository.findAll();
    }

    public void save(Address address, AppUser appUser) {
        LocalDateTime now = LocalDateTime.now();

        if (appUser != null) appUserRepository.save(appUser);

        addressRepository.save(address);
    }
}