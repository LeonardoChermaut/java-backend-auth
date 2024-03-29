package com.java.auth.service;
import com.java.auth.dto.CreateUserDto;
import com.java.auth.dto.UpdateUserDto;
import com.java.auth.dto.UserDto;
import com.java.auth.exception.UserException;
import com.java.auth.exception.UserNotFoundException;
import com.java.auth.model.UserModel;
import com.java.auth.repository.UserRepository;
import com.java.auth.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

	private ModelMapper mapper = new ModelMapper();
	@Autowired
	private UserRepository repository;

	@Autowired
	private UserUtil util;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public HttpStatus save(CreateUserDto dto) throws UserException {
		UserModel model = mapper.map(dto, UserModel.class);
		model.setNome(dto.getNome());
		model.setSobrenome(dto.getSobrenome());
		model.setSenha(passwordEncoder.encode(dto.getSenha()));
		model.setEmail((dto.getEmail()));
		if(repository.findByEmail(model.getEmail()).isPresent()) {
			throw new UserException();
		}
		repository.save(model);
		return HttpStatus.ACCEPTED;
	}

	@Transactional
	public void update(long id, UpdateUserDto dto) throws UserNotFoundException {
		UserModel model = this.repository.findById(id).orElseThrow(UserNotFoundException::new);
		model.setNome(dto.getNome());
		model.setEmail(dto.getEmail());
		model.setSobrenome(dto.getSobrenome());
		model.setSenha(dto.getSenha());
		repository.save(model);
	}
	@Transactional
	public void remove(long id) throws UserNotFoundException {
		objectOrThrow(id);
		repository.deleteById(id);
	}
	@Transactional
	public List<UserDto> allObjects() {
		return repository
				.findAll()
				.stream()
				.map(model -> mapper.map(model, UserDto.class))
				.collect(Collectors.toList());
	}
	@Transactional
	public UserDto findById(long id) throws UserNotFoundException {
		return objectOrThrow(id);
	}
	@Transactional
	public UserDto objectOrThrow(long id) throws UserNotFoundException {
		return repository
				.findById(id)
				.map(model -> mapper.map(model, UserDto.class))
				.orElseThrow(UserNotFoundException::new);
	}
	@Transactional
	public Optional<UserModel> context() {
		UserModel model = util.getUser();
		return repository.findByEmail(model.getEmail());
	}
}