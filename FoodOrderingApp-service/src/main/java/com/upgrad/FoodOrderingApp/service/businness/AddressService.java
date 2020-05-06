package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAdressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private CustomerAdressDao customerAdressDao;
    @Autowired
    CustomerService customerService;

    @Autowired
    private StateDao stateDao;


    public List<AddressEntity> getAddressList(CustomerEntity customerEntity) {
        List<AddressEntity> addressEntityList = new ArrayList<>();
        List<CustomerAddressEntity> customerAddressEntityList =
                addressDao.customerAddressByCustomer(customerEntity);
        if (customerAddressEntityList != null || !customerAddressEntityList.isEmpty()) {
            customerAddressEntityList.forEach(
                    customerAddressEntity -> addressEntityList.add(customerAddressEntity.getAddress()));
        }
        return addressEntityList;
    }

    public AddressEntity saveAddress(AddressEntity address, CustomerEntity customer) throws SaveAddressException {

        if (address.getActive() != null
                && address.getLocality() != null
                && !address.getLocality().isEmpty()
                && address.getCity() != null
                && !address.getCity().isEmpty()
                && address.getFlat_buil_number() != null
                && !address.getFlat_buil_number().isEmpty()
                && address.getPincode() != null
                && !address.getPincode().isEmpty()
                && address.getState() != null) {
            if (!isPincodeValid(address.getPincode())) {
                throw new SaveAddressException("SAR-002", "Invalid pincode");
            }

            AddressEntity addAddress = addressDao.createAddress(address);

            CustomerAddressEntity createdCustomerAddressEntity = new CustomerAddressEntity();
            createdCustomerAddressEntity.setCustomer(customer);
            createdCustomerAddressEntity.setAddress(addAddress);
            customerAdressDao.createCustomerAddress(createdCustomerAddressEntity);
            return addAddress;
        } else {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
    }

    public CustomerAddressEntity saveCustomerAddressRelation(CustomerAddressEntity customerAddress) throws AuthorizationFailedException, AddressNotFoundException {


        return customerAdressDao.createCustomerAddressRelation(customerAddress);

    }


    public String delete(String uuid, String accesstoken) throws AuthorizationFailedException, AddressNotFoundException {

        if(uuid.isEmpty())
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");

        customerService.checkAccessToken(accesstoken);
        if(addressDao.getAddressByUUID(uuid)==null)
            throw new AddressNotFoundException("ANF-003","No address by this id");

      return   addressDao.delete(uuid);
    }

    public List<StateEntity> getStateList() {
        return addressDao.getAllState();
    }

    /**
     * Returns state for a given UUID
     *
     * @param stateUuid UUID of the state entity
     * @return StateEntity object.
     * @throws AddressNotFoundException If given uuid does not exist in database.
     */
    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException {
        if (stateDao.findStateByUUID(stateUuid) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return stateDao.findStateByUUID(stateUuid);
    }

    /**
     * This method implements logic for getting the Address using address uuid.
     *
     * @param addressId Address UUID.
     * @param customerEntity Customer whose addresses has to be fetched.
     * @return AddressEntity object.
     * @throws AddressNotFoundException If any validation on address fails.
     * @throws AuthorizationFailedException If any validation on customer fails.
     */
    public AddressEntity getAddressByUUID(final String addressId, final CustomerEntity customerEntity)
            throws AuthorizationFailedException, AddressNotFoundException {
        AddressEntity addressEntity = addressDao.getAddressByUUID(addressId);
        if (addressId.isEmpty()) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        CustomerAddressEntity customerAddressEntity =
                customerAdressDao.customerAddressByAddress(addressEntity);
        if (!customerAddressEntity.getCustomer().getUuid().equals(customerEntity.getUuid())) {
            throw new AuthorizationFailedException(
                    "ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        return addressEntity;
    }

    // checks if the pincode entered is invalid (i.e it does not include only numbers or its size is not six)
    private boolean isPincodeValid(final String pincode) {
        if (pincode.length() != 6) {
            return false;
        }
        for (int i = 0; i < pincode.length(); i++) {
            if (!Character.isDigit(pincode.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
