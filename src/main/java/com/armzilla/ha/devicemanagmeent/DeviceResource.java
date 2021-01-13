package com.armzilla.ha.devicemanagmeent;

import com.armzilla.ha.api.Device;
import com.armzilla.ha.dao.DeviceDescriptor;
import com.armzilla.ha.dao.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.*;

/**
 * Created by arm on 4/13/15.
 */
@Controller
@RequestMapping("/api/devices")
public class DeviceResource {

    private static final Set<String> supportedVerbs = new HashSet<>(Arrays.asList("get", "put", "post"));

    @Autowired
    private DeviceRepository deviceRepository;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", headers = "Accept=application/json")
    public ResponseEntity<DeviceDescriptor> createDevice(@RequestBody Device device) {
        if(device.getContentBody() != null ) {
            if (device.getContentType() == null || device.getHttpVerb() == null || !supportedVerbs.contains(device.getHttpVerb().toLowerCase())) {
                return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
            }
        } //add more validation like content type
        DeviceDescriptor deviceEntry = new DeviceDescriptor();
        deviceEntry.setId(UUID.randomUUID().toString());
        deviceEntry.setName(device.getName());
        deviceEntry.setDeviceType(device.getDeviceType());
        deviceEntry.setOnUrl(device.getOnUrl());
        deviceEntry.setOffUrl(device.getOffUrl());
        deviceEntry.setContentType(device.getContentType());
        deviceEntry.setContentBody(device.getContentBody());
        deviceEntry.setHttpVerb(device.getHttpVerb());

        deviceRepository.save(deviceEntry);

        return new ResponseEntity<>(deviceEntry, null, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{lightId}", method = RequestMethod.PUT, produces = "application/json", headers = "Accept=application/json")
    public ResponseEntity<DeviceDescriptor> updateDevice(@PathVariable("lightId") String id, @RequestBody Device device) {
	Optional<DeviceDescriptor> d = deviceRepository.findById(id);
	if (! d.isPresent())
        {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
        DeviceDescriptor deviceEntry = d.get();

        deviceEntry.setName(device.getName());
        deviceEntry.setDeviceType(device.getDeviceType());
        deviceEntry.setOnUrl(device.getOnUrl());
        deviceEntry.setOffUrl(device.getOffUrl());

        deviceRepository.save(deviceEntry);

        return new ResponseEntity<>(deviceEntry, null, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<DeviceDescriptor>> findAllDevices() {

        Iterable<DeviceDescriptor> iterable = deviceRepository.findAll();
        List<DeviceDescriptor> deviceList = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

        List<DeviceDescriptor> plainList = new LinkedList<>(deviceList);
        return new ResponseEntity<>(plainList, null, HttpStatus.OK);
    }

    @RequestMapping(value = "/{lightId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<DeviceDescriptor> findByDeviceId(@PathVariable("lightId") String id){
	Optional<DeviceDescriptor> d = deviceRepository.findById(id);

        if(! d.isPresent())
        {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
        DeviceDescriptor descriptor = d.get();
        return new ResponseEntity<>(descriptor, null, HttpStatus.OK);
    }

    @RequestMapping(value = "/{lightId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> deleteDeviceById(@PathVariable("lightId") String id){
	Optional<DeviceDescriptor> d = deviceRepository.findById(id);

        if(! d.isPresent())
        {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
        DeviceDescriptor deleted = d.get();
        deviceRepository.delete(deleted);
        return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
    }


}
