package com.example.demo.Controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.Models.Client;
import com.example.demo.Repositories.ClientRepository;

@CrossOrigin(origins="http://localhost:4200", maxAge=3600)
@RestController
@RequestMapping("/api/v1")
public class ClientController {
	public static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	@Autowired
	private ClientRepository clientRepository;
	
	@GetMapping(path="/clients", produces="application/json")
	public ResponseEntity<List<Client>> getClients() {		
		List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Client>>(clients, HttpStatus.OK);
	}
	
	@GetMapping("/clients/{id}")
	public ResponseEntity<?> retrieveStudent(@PathVariable long id) {
		Optional<Client> client = clientRepository.findById(id);

		if (!client.isPresent())
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<Client>(client.get(), HttpStatus.OK);
	}	
	
	@PostMapping(path="/clients", consumes="application/json", produces="application/json")
	public ResponseEntity<?> save(@RequestBody Client client, UriComponentsBuilder builder) {
		Client savedClient = clientRepository.save(client);
		
		if(savedClient == null)
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/clients/{id}").buildAndExpand(savedClient.getId()).toUri());
        
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	
	@PutMapping(path = "/clients/{id}", consumes="application/json", produces="application/json")
	public ResponseEntity<Object> updateStudent(@RequestBody Client client, @PathVariable long id) {

		Optional<Client> clientOptional = clientRepository.findById(id);

		if (!clientOptional.isPresent())
			return ResponseEntity.notFound().build();

		client.setId(id);
		
		clientRepository.save(client);

		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/clients/{id}")
	public void deleteStudent(@PathVariable long id) {
		clientRepository.deleteById(id);
	}
}
