package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.dao.Customer.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerAdressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
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


    public List<Address> getAddressList(Customers customerEntity) {
        List<Address> addressEntityList = new ArrayList<>();
        List<CustomerAddress> customerAddressEntityList =
                addressDao.customerAddressByCustomer(customerEntity);
        if (customerAddressEntityList != null || !customerAddressEntityList.isEmpty()) {
            customerAddressEntityList.forEach(
                    customerAddressEntity -> addressEntityList.add(customerAddressEntity.getAddress()));
        }
        return addressEntityList;
    }

    public Address saveAddress(Address address, Customers customer) throws SaveAddressException {

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

            Address addAddress = addressDao.createAddress(address);

            CustomerAddress createdCustomerAddressEntity = new CustomerAddress();
            createdCustomerAddressEntity.setCustomer(customer);
            createdCustomerAddressEntity.setAddress(addAddress);
            customerAdressDao.createCustomerAddress(createdCustomerAddressEntity);
            return addAddress;
        } else {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
    }

    public CustomerAddress saveCustomerAddressRelation(CustomerAddress customerAddress) throws AuthorizationFailedException, AddressNotFoundException {


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
