package com.amars.frontend.controller;

import com.amars.frontend.dto.*;
import com.amars.frontend.model.CustomUserDetails;
import com.amars.frontend.model.User;
import com.amars.frontend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class FrontendController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    String hello(){
        return "index.html";
    }

    @RequestMapping("/")
    String helloWorld(){
        return "index.html";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user) throws JsonProcessingException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setUserCode(RandomStringUtils.randomAlphanumeric(30));
        userRepository.save(user);

        CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentCode = userSecurity.getUserCode();

        AuthinfoCreateDTO authinfoCreateDTO = AuthinfoCreateDTO
                .builder()
                .current_user(currentCode)
                .role(user.getRole())
                .user_code(user.getUserCode())
                .build();

        sendAuth(authinfoCreateDTO);

        return "redirect:/users";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        if(isAuthenticated()) {
            List<User> listUsers = userRepository.findAllUsers();
            model.addAttribute("listUsers", listUsers);
            return "users";
        }
        else return "redirect:/";
    }

    @GetMapping("/students")
    public String showStudents(Model model) {
        if(isAuthenticated()) {
            List<User> listStudents = userRepository.findAllStudents();
            model.addAttribute("listStudents", listStudents);
            return "students";
        }
        else return "redirect:/";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    private void sendAuth(AuthinfoCreateDTO authinfoCreateDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/auth/create";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(authinfoCreateDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    @PostMapping("/process_student")
    public String processStudent(StudentDTO studentDTO, String username, String password) throws JsonProcessingException {
        User user = User
                .builder()
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .role("student")
                .password("")
                .username(username)
                .userCode("")
                .build();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);
        user.setUserCode(RandomStringUtils.randomAlphanumeric(30));
        userRepository.save(user);

        CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentCode = userSecurity.getUserCode();

        AuthinfoCreateDTO authinfoCreateDTO = AuthinfoCreateDTO
                .builder()
                .current_user(currentCode)
                .role(user.getRole())
                .user_code(user.getUserCode())
                .build();

        StudentDTO studentToSave = StudentDTO
                .builder()
                .studentCode(user.getUserCode())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .identificationNumber(studentDTO.getIdentificationNumber())
                .birthDate(studentDTO.getBirthDate())
                .year(studentDTO.getYear())
                .department(studentDTO.getDepartment())
                .major(studentDTO.getMajor())
                .status(studentDTO.getStatus())
                .build();

        //ovdje dodajem create wallet

        StudentRequestDTO studentRequestDTO = StudentRequestDTO
                .builder()
                .studentCode(user.getUserCode())
                .build();

        sendAuth(authinfoCreateDTO);
        sendStudent(studentToSave);
        sendWallet(studentRequestDTO);

        return "redirect:/students";
    }

    private void sendWallet(StudentRequestDTO studentRequestDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/wallet/createWallet";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(studentRequestDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    private void sendStudent(StudentDTO studentDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/student/create";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(studentDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    @RequestMapping("/students/{userCode}")
    public String showStudents(Model model, @PathVariable(name = "userCode") String userCode) throws Exception{
        if(isAuthenticated()) {
            StudentRequestDTO studentRequestDTO = StudentRequestDTO
                    .builder()
                    .studentCode(userCode)
                    .build();

            String resourceUrl
                    = "http://localhost:8080/api/student/fullstudentinfo";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(studentRequestDTO);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String studentJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(studentJson);
            JsonObject rootObj = root.getAsJsonObject();


            StudentResponseFullDTO studentResponseDTO = StudentResponseFullDTO
                    .builder()
                    .identificationNumber(rootObj.get("identificationNumber").getAsString())
                    .studentCode(rootObj.get("studentCode").getAsString())
                    .firstName(rootObj.get("firstName").getAsString())
                    .lastName(rootObj.get("lastName").getAsString())
                    .birthDate(rootObj.get("birthDate").getAsString())
                    .year(rootObj.get("year").getAsInt())
                    .department(rootObj.get("department").getAsString())
                    .major(rootObj.get("major").getAsString())
                    .status(rootObj.get("status").getAsString())
                    .build();

            model.addAttribute("student", studentResponseDTO);
            return "studentByCode";
        }
        else return "redirect:/";
    }

    @PostMapping("/update_student")
    public String updateStudent(StudentDTO studentDTO) throws JsonProcessingException {

        StudentDTO studentToSave = StudentDTO
                .builder()
                .studentCode(studentDTO.getStudentCode())
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .identificationNumber(studentDTO.getIdentificationNumber())
                .birthDate(studentDTO.getBirthDate())
                .year(studentDTO.getYear())
                .department(studentDTO.getDepartment())
                .major(studentDTO.getMajor())
                .status(studentDTO.getStatus())
                .build();

        sendStudent(studentToSave);

        return "redirect:/students";
    }

    @GetMapping("/departments")
    public String showDepartments(Model model) throws Exception{
        if(isAuthenticated()) {

            String resourceUrl
                    = "http://localhost:8080/api/department/departments";

            URL url = new URL(resourceUrl);
            URLConnection request = url.openConnection();
            request.connect();

            List<DepartmentDTO> listDepartments = new ArrayList<DepartmentDTO>();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray rootobjArray = root.getAsJsonArray();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                String description = rootobj.get("description").getAsString();
                listDepartments.add(
                        DepartmentDTO
                                .builder()
                                .name(name)
                                .description(description)
                                .build()
                );
            }

            model.addAttribute("listDepartments", listDepartments);
            return "departments";
        }
        else return "redirect:/";
    }

    @PostMapping("/process_department")
    public String createDepartment(DepartmentDTO departmentDTO) throws JsonProcessingException {

        DepartmentDTO department = DepartmentDTO
                .builder()
                .name(departmentDTO.getName())
                .description(departmentDTO.getDescription())
                .build();

        sendDepartment(department);

        return "redirect:/departments";
    }

    private void sendDepartment(DepartmentDTO departmentDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/department/createDepartment";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(departmentDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    @GetMapping("/subjects")
    public String showSubjects(Model model) throws Exception{
        if(isAuthenticated()) {

            String resourceUrl
                    = "http://localhost:8080/api/subject/subjects";

            URL url = new URL(resourceUrl);
            URLConnection request = url.openConnection();
            request.connect();

            List<SubjectSpecialDTO> listSubjects = new ArrayList<SubjectSpecialDTO>();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray rootobjArray = root.getAsJsonArray();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                String description = rootobj.get("description").getAsString();
                String professor = rootobj.get("professor").getAsString();
                String professorCode = rootobj.get("professorCode").getAsString();
                String major = rootobj.get("major").getAsString();
                Long id = rootobj.get("id").getAsLong();
                listSubjects.add(
                        SubjectSpecialDTO
                                .builder()
                                .name(name)
                                .id(id)
                                .professor(professor)
                                .professorCode(professorCode)
                                .major(major)
                                .description(description)
                                .build()
                );
            }

            model.addAttribute("listSubjects", listSubjects);
            return "subjects";
        }
        else return "redirect:/";
    }

    @PostMapping("/process_subject")
    public String createSubject(SubjectDTO subjectDTO) throws JsonProcessingException {

        SubjectDTO subject = SubjectDTO
                .builder()
                .name(subjectDTO.getName())
                .description(subjectDTO.getDescription())
                .professor(subjectDTO.getProfessor())
                .professorCode(subjectDTO.getProfessorCode())
                .major(subjectDTO.getMajor())
                .build();

        sendSubject(subject);

        return "redirect:/subjects";
    }

    private void sendSubject(SubjectDTO subjectDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/subject/createSubject";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(subjectDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    @GetMapping("/departmentSubject")
    public String addSubjectsToDepartment(Model model) throws Exception{
        if(isAuthenticated()) {

            String resourceUrl
                    = "http://localhost:8080/api/subject/subjects";

            URL url = new URL(resourceUrl);
            URLConnection request = url.openConnection();
            request.connect();

            List<SubjectDTO> listSubjects = new ArrayList<SubjectDTO>();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray rootobjArray = root.getAsJsonArray();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                String description = rootobj.get("description").getAsString();
                String professor = rootobj.get("professor").getAsString();
                String major = rootobj.get("major").getAsString();
                listSubjects.add(
                        SubjectDTO
                                .builder()
                                .name(name)
                                .professor(professor)
                                .major(major)
                                .description(description)
                                .build()
                );
            }

            String resourceUrl1
                    = "http://localhost:8080/api/department/departments";

            URL url1 = new URL(resourceUrl1);
            URLConnection request1 = url1.openConnection();
            request1.connect();

            List<DepartmentDTO> listDepartments = new ArrayList<DepartmentDTO>();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(new InputStreamReader((InputStream) request1.getContent()));
            JsonArray rootobjArray1 = root1.getAsJsonArray();

            for(int i=0;i<rootobjArray1.size();i++){
                JsonObject rootobj1 = rootobjArray1.get(i).getAsJsonObject();
                String name = rootobj1.get("name").getAsString();
                String description = rootobj1.get("description").getAsString();
                listDepartments.add(
                        DepartmentDTO
                                .builder()
                                .name(name)
                                .description(description)
                                .build()
                );
            }

            model.addAttribute("listDepartments", listDepartments);
            model.addAttribute("listSubjects", listSubjects);

            return "departmentSubject";
        }
        else return "redirect:/";
    }

    @PostMapping("/process_subject_department")
    public String createSubjectDep(CreateSubjectDepartmentDTO createSubjectDepartmentDTO) throws JsonProcessingException {

        CreateSubjectDepartmentDTO subjectDepartmentDTO = CreateSubjectDepartmentDTO
                .builder()
                .name(createSubjectDepartmentDTO.getName())
                .year(createSubjectDepartmentDTO.getYear())
                .departmentName(createSubjectDepartmentDTO.getDepartmentName())
                .build();

        sendSubjectDepCreation(createSubjectDepartmentDTO);

        return "redirect:/departmentSubject";
    }

    private void sendSubjectDepCreation(CreateSubjectDepartmentDTO createSubjectDepartmentDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/department/createSubject";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(createSubjectDepartmentDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    @RequestMapping("/departmentSubjects/{departmentName}")
    public String showSubjectsOnDepartment(Model model, @PathVariable(name = "departmentName") String departmentName) throws Exception{
        if(isAuthenticated()) {
            SubjectDepDTO subjectDepDTO = SubjectDepDTO
                    .builder()
                    .name(departmentName)
                    .year(Integer.toUnsignedLong(1))
                    .build();

            String resourceUrl
                    = "http://localhost:8080/api/department/subjects";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(subjectDepDTO);
            //System.out.println(jsonString);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String studentJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(studentJson);
            JsonArray rootobjArray = root.getAsJsonArray();

            List<SubjectDepDTO> listSubjectDepsYear1 = new ArrayList<SubjectDepDTO>();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                Long year = rootobj.get("year").getAsLong();
                listSubjectDepsYear1.add(
                        SubjectDepDTO
                                .builder()
                                .name(name)
                                .year(year)
                                .build()
                );
            }

            SubjectDepDTO subjectDepDTO2 = SubjectDepDTO
                    .builder()
                    .name(departmentName)
                    .year(Integer.toUnsignedLong(2))
                    .build();


            RestTemplate restTemplate2 = new RestTemplate();
            String jsonString2 = mapper.writeValueAsString(subjectDepDTO2);
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity2 = new HttpEntity<String>(jsonString2, headers2);
            ResponseEntity<String> response2 = restTemplate2.postForEntity(resourceUrl, entity2, String.class);

            String studentJson2 = response2.getBody();

            JsonParser jp2 = new JsonParser();
            JsonElement root2 = jp2.parse(studentJson2);
            JsonArray rootobjArray2 = root2.getAsJsonArray();

            List<SubjectDepDTO> listSubjectDepsYear2 = new ArrayList<SubjectDepDTO>();

            for(int i=0;i<rootobjArray2.size();i++){
                JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                Long year = rootobj.get("year").getAsLong();
                listSubjectDepsYear2.add(
                        SubjectDepDTO
                                .builder()
                                .name(name)
                                .year(year)
                                .build()
                );
            }

            SubjectDepDTO subjectDepDTO3 = SubjectDepDTO
                    .builder()
                    .name(departmentName)
                    .year(Integer.toUnsignedLong(3))
                    .build();


            RestTemplate restTemplate3 = new RestTemplate();
            String jsonString3 = mapper.writeValueAsString(subjectDepDTO3);
            HttpHeaders headers3 = new HttpHeaders();
            headers3.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity3 = new HttpEntity<String>(jsonString3, headers3);
            ResponseEntity<String> response3 = restTemplate3.postForEntity(resourceUrl, entity3, String.class);

            String studentJson3 = response3.getBody();

            JsonParser jp3 = new JsonParser();
            JsonElement root3 = jp3.parse(studentJson3);
            JsonArray rootobjArray3 = root3.getAsJsonArray();

            List<SubjectDepDTO> listSubjectDepsYear3 = new ArrayList<SubjectDepDTO>();

            for(int i=0;i<rootobjArray3.size();i++){
                JsonObject rootobj = rootobjArray3.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                Long year = rootobj.get("year").getAsLong();
                listSubjectDepsYear3.add(
                        SubjectDepDTO
                                .builder()
                                .name(name)
                                .year(year)
                                .build()
                );
            }

            SubjectDepDTO subjectDepDTO4 = SubjectDepDTO
                    .builder()
                    .name(departmentName)
                    .year(Integer.toUnsignedLong(4))
                    .build();


            RestTemplate restTemplate4 = new RestTemplate();
            String jsonString4 = mapper.writeValueAsString(subjectDepDTO4);
            HttpHeaders headers4 = new HttpHeaders();
            headers4.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity4 = new HttpEntity<String>(jsonString4, headers4);
            ResponseEntity<String> response4 = restTemplate4.postForEntity(resourceUrl, entity4, String.class);

            String studentJson4 = response4.getBody();

            JsonParser jp4 = new JsonParser();
            JsonElement root4 = jp4.parse(studentJson4);
            JsonArray rootobjArray4 = root4.getAsJsonArray();

            List<SubjectDepDTO> listSubjectDepsYear4 = new ArrayList<SubjectDepDTO>();

            for(int i=0;i<rootobjArray4.size();i++){
                JsonObject rootobj = rootobjArray4.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                Long year = rootobj.get("year").getAsLong();
                listSubjectDepsYear4.add(
                        SubjectDepDTO
                                .builder()
                                .name(name)
                                .year(year)
                                .build()
                );
            }

            model.addAttribute("listSubjectDepsYear1", listSubjectDepsYear1);
            model.addAttribute("listSubjectDepsYear2", listSubjectDepsYear2);
            model.addAttribute("listSubjectDepsYear3", listSubjectDepsYear3);
            model.addAttribute("listSubjectDepsYear4", listSubjectDepsYear4);

            return "subjectListForDepartment";
        }
        else return "redirect:/";
    }

    @RequestMapping("/updateSubjectList/{userCode}")
    public String updateSubjectList(Model model, @PathVariable(name = "userCode") String userCode) throws Exception{
        if(isAuthenticated()) {
            StudentRequestDTO studentRequestDTO = StudentRequestDTO
                    .builder()
                    .studentCode(userCode)
                    .build();

            String resourceUrl
                    = "http://localhost:8080/api/student/fullstudentinfo";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(studentRequestDTO);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String studentJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(studentJson);
            JsonObject rootObj = root.getAsJsonObject();


            StudentResponseFullDTO studentResponseDTO = StudentResponseFullDTO
                    .builder()
                    .identificationNumber(rootObj.get("identificationNumber").getAsString())
                    .studentCode(rootObj.get("studentCode").getAsString())
                    .firstName(rootObj.get("firstName").getAsString())
                    .lastName(rootObj.get("lastName").getAsString())
                    .birthDate(rootObj.get("birthDate").getAsString())
                    .year(rootObj.get("year").getAsInt())
                    .department(rootObj.get("department").getAsString())
                    .major(rootObj.get("major").getAsString())
                    .status(rootObj.get("status").getAsString())
                    .build();

            SubjectDepDTO subjectDepDTO = SubjectDepDTO
                    .builder()
                    .name(studentResponseDTO.getDepartment())
                    .year(Integer.toUnsignedLong(studentResponseDTO.getYear()))
                    .build();

            String resourceUrl1
                    = "http://localhost:8080/api/department/subjects";

            RestTemplate restTemplate1 = new RestTemplate();
            String jsonString1 = mapper.writeValueAsString(subjectDepDTO);
            //System.out.println(jsonString);
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity1 = new HttpEntity<String>(jsonString1, headers1);
            ResponseEntity<String> response1 = restTemplate.postForEntity(resourceUrl1, entity1, String.class);

            String studentJson1 = response1.getBody();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(studentJson1);
            JsonArray rootobjArray1 = root1.getAsJsonArray();

            List<SubjectDepDTO> listSubjectDepsYear = new ArrayList<SubjectDepDTO>();

            for(int i=0;i<rootobjArray1.size();i++){
                JsonObject rootobj = rootobjArray1.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                Long year = rootobj.get("year").getAsLong();
                listSubjectDepsYear.add(
                        SubjectDepDTO
                                .builder()
                                .name(name)
                                .year(year)
                                .build()
                );
            }

            String resourceUrl2
                    = "http://localhost:8080/api/subject/subjects";

            URL url2 = new URL(resourceUrl2);
            URLConnection request2 = url2.openConnection();
            request2.connect();

            List<SubjectDTO> listSubjects = new ArrayList<SubjectDTO>();

            JsonParser jp2 = new JsonParser();
            JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request2.getContent()));
            JsonArray rootobjArray2 = root2.getAsJsonArray();

            for(int i=0;i<rootobjArray2.size();i++){
                JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                String description = rootobj.get("description").getAsString();
                String professor = rootobj.get("professor").getAsString();
                String major = rootobj.get("major").getAsString();
                if(major.equals(studentResponseDTO.getMajor()) || major.equals("Opći"))
                listSubjects.add(
                        SubjectDTO
                                .builder()
                                .name(name)
                                .professor(professor)
                                .major(major)
                                .description(description)
                                .build()
                );
            }

            List<String> sbList = new ArrayList<String>();

            for(SubjectDepDTO sbDp : listSubjectDepsYear){
                for(SubjectDTO sb : listSubjects){
                    if(sb.getName().equals(sbDp.getName()))
                        sbList.add(sb.getName());
                }
            }

            for(String name : sbList){
                sendSubjectStudentCreation(
                        StudentSubjectDTO
                                .builder()
                                .subject(name)
                                .grade(0)
                                .score(0)
                                .studentCode(studentResponseDTO.getStudentCode())
                                .complete(false)
                                .date(LocalDate.now().toString())
                                .build()
                );
            }

            model.addAttribute("student", studentResponseDTO);
            return "studentByCode";
        }
        else return "redirect:/";
    }

    private void sendSubjectStudentCreation(StudentSubjectDTO createStudentSubjectDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/subject/addStudentToSubject";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(createStudentSubjectDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    //tu smo

    @RequestMapping("/showCompleteSubjects/{userCode}")
    public String showCompleteSubjects(Model model, @PathVariable(name = "userCode") String userCode) throws Exception{
        if(isAuthenticated()) {
            StudentRequestDTO studentRequestDTO = StudentRequestDTO
                    .builder()
                    .studentCode(userCode)
                    .build();

            String resourceUrl1
                    = "http://localhost:8080/api/subject/showCompleteSubjects";


            RestTemplate restTemplate1 = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString1 = mapper.writeValueAsString(studentRequestDTO);
            //System.out.println(jsonString);
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity1 = new HttpEntity<String>(jsonString1, headers1);
            ResponseEntity<String> response1 = restTemplate1.postForEntity(resourceUrl1, entity1, String.class);

            String studentJson1 = response1.getBody();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(studentJson1);
            JsonArray rootobjArray1 = root1.getAsJsonArray();

            List<StudentSubjectDTO> listSubjects = new ArrayList<StudentSubjectDTO>();

            for(int i=0;i<rootobjArray1.size();i++){
                JsonObject rootObj = rootobjArray1.get(i).getAsJsonObject();
                listSubjects.add(
                        StudentSubjectDTO
                                .builder()
                                .studentCode(rootObj.get("studentCode").getAsString())
                                .subject(rootObj.get("subject").getAsString())
                                .score(rootObj.get("score").getAsInt())
                                .grade(rootObj.get("grade").getAsInt())
                                .date(rootObj.get("date").getAsString())
                                .complete(rootObj.get("complete").getAsBoolean())
                                .build()
                );
            }




            model.addAttribute("listSubjects", listSubjects);
            return "studentCompleteSubjects";
        }
        else return "redirect:/";
    }

    @RequestMapping("/showIncompleteSubjects/{userCode}")
    public String showIncompleteSubjects(Model model, @PathVariable(name = "userCode") String userCode) throws Exception{
        if(isAuthenticated()) {
            StudentRequestDTO studentRequestDTO = StudentRequestDTO
                    .builder()
                    .studentCode(userCode)
                    .build();

            String resourceUrl1
                    = "http://localhost:8080/api/subject/showIncompleteSubjects";


            RestTemplate restTemplate1 = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString1 = mapper.writeValueAsString(studentRequestDTO);
            //System.out.println(jsonString);
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity1 = new HttpEntity<String>(jsonString1, headers1);
            ResponseEntity<String> response1 = restTemplate1.postForEntity(resourceUrl1, entity1, String.class);

            String studentJson1 = response1.getBody();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(studentJson1);
            JsonArray rootobjArray1 = root1.getAsJsonArray();

            List<StudentSubjectDTO> listSubjects = new ArrayList<StudentSubjectDTO>();

            for(int i=0;i<rootobjArray1.size();i++){
                JsonObject rootObj = rootobjArray1.get(i).getAsJsonObject();
                listSubjects.add(
                        StudentSubjectDTO
                                .builder()
                                .studentCode(rootObj.get("studentCode").getAsString())
                                .subject(rootObj.get("subject").getAsString())
                                .score(rootObj.get("score").getAsInt())
                                .grade(rootObj.get("grade").getAsInt())
                                .date(rootObj.get("date").getAsString())
                                .complete(rootObj.get("complete").getAsBoolean())
                                .build()
                );
            }




            model.addAttribute("listSubjects", listSubjects);
            return "studentCompleteSubjects";
        }
        else return "redirect:/";
    }

    @GetMapping("/exams")
    public String showExams(Model model) throws Exception{
        if(isAuthenticated()) {

            CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentCode = userSecurity.getUserCode();
            String currentRole = userSecurity.getRole();


            String resourceUrl
                    = "http://localhost:8080/api/subject/subjects";

            URL url = new URL(resourceUrl);
            URLConnection request = url.openConnection();
            request.connect();

            List<SubjectSpecialDTO> listSubjects = new ArrayList<SubjectSpecialDTO>();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray rootobjArray = root.getAsJsonArray();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                String description = rootobj.get("description").getAsString();
                Long id = rootobj.get("id").getAsLong();
                String professor = rootobj.get("professor").getAsString();
                String major = rootobj.get("major").getAsString();
                if(rootobj.get("professorCode").getAsString().equals(currentCode) || currentRole.equals("admin"))
                    listSubjects.add(
                            SubjectSpecialDTO
                                    .builder()
                                    .id(id)
                                    .name(name)
                                    .professor(professor)
                                    .major(major)
                                    .description(description)
                                    .build()
                    );
            }

            model.addAttribute("listSubjects", listSubjects);
            return "exams";
        }
        else return "redirect:/";
    }

    @GetMapping("/examsStudent")
    public String showExamsStudent(Model model) throws Exception{
        if(isAuthenticated()) {
            CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentCode = userSecurity.getUserCode();
            String currentRole = userSecurity.getRole();

            StudentRequestDTO studentRequestDTO = StudentRequestDTO
                    .builder()
                    .studentCode(currentCode)
                    .build();

            String resourceUrl1
                    = "http://localhost:8080/api/subject/showIncompleteSubjects";


            RestTemplate restTemplate1 = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString1 = mapper.writeValueAsString(studentRequestDTO);
            //System.out.println(jsonString);
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity1 = new HttpEntity<String>(jsonString1, headers1);
            ResponseEntity<String> response1 = restTemplate1.postForEntity(resourceUrl1, entity1, String.class);

            String studentJson1 = response1.getBody();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(studentJson1);
            JsonArray rootobjArray1 = root1.getAsJsonArray();

            List<StudentSubjectDTO> listSubjects = new ArrayList<StudentSubjectDTO>();

            for(int i=0;i<rootobjArray1.size();i++){
                JsonObject rootObj = rootobjArray1.get(i).getAsJsonObject();
                listSubjects.add(
                        StudentSubjectDTO
                                .builder()
                                .studentCode(rootObj.get("studentCode").getAsString())
                                .subject(rootObj.get("subject").getAsString())
                                .score(rootObj.get("score").getAsInt())
                                .grade(rootObj.get("grade").getAsInt())
                                .date(rootObj.get("date").getAsString())
                                .complete(rootObj.get("complete").getAsBoolean())
                                .build()
                );
            }

            String resourceUrl
                    = "http://localhost:8080/api/subject/subjects";

            URL url = new URL(resourceUrl);
            URLConnection request = url.openConnection();
            request.connect();

            List<SubjectSpecialDTO> listActualSubjects = new ArrayList<SubjectSpecialDTO>();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray rootobjArray = root.getAsJsonArray();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                String name = rootobj.get("name").getAsString();
                String description = rootobj.get("description").getAsString();
                String professor = rootobj.get("professor").getAsString();
                Long id = rootobj.get("id").getAsLong();
                String major = rootobj.get("major").getAsString();
                boolean doable = false;
                for (StudentSubjectDTO sb : listSubjects){
                    if(sb.getSubject().equals(rootobj.get("name").getAsString()))
                        doable = true;
                }
                if(doable) {
                    listActualSubjects.add(
                            SubjectSpecialDTO
                                    .builder()
                                    .id(id)
                                    .name(name)
                                    .professor(professor)
                                    .major(major)
                                    .description(description)
                                    .build()
                    );
                }

            }


            model.addAttribute("listSubjects", listActualSubjects);

            return "exams";
        }
        else return "redirect:/";
    }

    @RequestMapping("/examSubject/{name}")
    public String showExamsForSubject(Model model, @PathVariable(name = "name") String name) throws Exception{
        if(isAuthenticated()) {

            CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentCode = userSecurity.getUserCode();

            ExamRequestDTO examRequestDTO1 = ExamRequestDTO
                    .builder()
                    .subject(currentCode)
                    .build();


            String resourceUrl
                    = "http://localhost:8080/api/exam/getRegisteredExams";


            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(examRequestDTO1);
            //System.out.println(jsonString);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String examJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(examJson);
            JsonArray rootobjArray = root.getAsJsonArray();

            List<ExamIdsDTO> listExamIds = new ArrayList<ExamIdsDTO>();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootObj = rootobjArray.get(i).getAsJsonObject();
                listExamIds.add(
                        ExamIdsDTO
                                .builder()
                                .idExam(rootObj.get("idExam").getAsInt())
                                .build()
                );
            }

            //izmjene - treba subject name od tog id
            String resourceUrl2
                    = "http://localhost:8080/api/subject/subjects";

            URL url2 = new URL(resourceUrl2);
            URLConnection request2 = url2.openConnection();
            request2.connect();


            JsonParser jp2 = new JsonParser();
            JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request2.getContent()));
            JsonArray rootobjArray2 = root2.getAsJsonArray();

            String subName = new String();

            for(int i=0;i<rootobjArray2.size();i++){
                JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
                String name1 = rootobj.get("name").getAsString();
                Long id = rootobj.get("id").getAsLong();
                if(name.equals(id.toString()))
                    subName = name1;
            }



            //gg
            ExamRequestDTO examRequestDTO = ExamRequestDTO
                    .builder()
                    .subject(subName)
                    .build();

            String resourceUrl1
                    = "http://localhost:8080/api/exam/exams";


            RestTemplate restTemplate1 = new RestTemplate();
            String jsonString1 = mapper.writeValueAsString(examRequestDTO);
            //System.out.println(jsonString);
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity1 = new HttpEntity<String>(jsonString1, headers1);
            ResponseEntity<String> response1 = restTemplate1.postForEntity(resourceUrl1, entity1, String.class);

            String examJson1 = response1.getBody();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(examJson1);
            JsonArray rootobjArray1 = root1.getAsJsonArray();

            List<ExamResponseDTO> listExams = new ArrayList<ExamResponseDTO>();

            for(int i=0;i<rootobjArray1.size();i++) {
                JsonObject rootObj = rootobjArray1.get(i).getAsJsonObject();
                boolean doable = true;
                for (ExamIdsDTO examId : listExamIds){
                    if(examId.getIdExam() == rootObj.get("id").getAsInt())
                        doable = false;
                }
                if(doable) {
                    listExams.add(
                            ExamResponseDTO
                                    .builder()
                                    .id(rootObj.get("id").getAsLong())
                                    .subject(rootObj.get("subject").getAsString())
                                    .date(rootObj.get("date").getAsString())
                                    .build()
                    );
                }
            }



            model.addAttribute("subjectName", name);
            model.addAttribute("listExams", listExams);
            return "examsBySubject";
        }
        else return "redirect:/";
    }

    @PostMapping("/process_exam")
    public String createExam(ExamCreateDTO examCreateDTO) throws Exception {

        String resourceUrl2
                = "http://localhost:8080/api/subject/subjects";

        URL url2 = new URL(resourceUrl2);
        URLConnection request2 = url2.openConnection();
        request2.connect();


        JsonParser jp2 = new JsonParser();
        JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request2.getContent()));
        JsonArray rootobjArray2 = root2.getAsJsonArray();

        String subName = new String();
        String idS = examCreateDTO.getSubject();

        for(int i=0;i<rootobjArray2.size();i++){
            JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
            String name1 = rootobj.get("name").getAsString();
            String id = rootobj.get("id").getAsString();
            if(idS.equals(id))
                subName = name1;
        }


        ExamCreateDTO examToCreate = ExamCreateDTO
                .builder()
                .subject(subName)
                .date(examCreateDTO.getDate())
                .build();

        sendExam(examToCreate);

        return "redirect:/examSubject/"+examCreateDTO.getSubject();
    }

    private void sendExam(ExamCreateDTO examCreateDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/exam/createExam";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(examCreateDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }


    @PostMapping("/register_exam")
    public String registerExam(ExamRegisterDTO examRegisterDTO) throws Exception {

        CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentCode = userSecurity.getUserCode();

        ExamRegisterDTO examToRegister = ExamRegisterDTO
                .builder()
                .idExam(examRegisterDTO.getIdExam())
                .studentCode(currentCode)
                .build();

        String status = getUserStatusFromCode(currentCode);

        if(status.equals("redovni") || status.equals("obnova") || status.equals("apsolvent")){
            payFromWallet(
                    PaymentRequestDTO.builder()
                            .amount(1)
                            .studentCode(currentCode)
                            .build()
            );
            sendExamRegistration(examToRegister);
        }

        if(status.equals("vandredni") || status.equals("samofinansirajuci") ){
            payFromWallet(
                    PaymentRequestDTO.builder()
                            .amount(5)
                            .studentCode(currentCode)
                            .build()
            );
            sendExamRegistration(examToRegister);
        }

        if(status.equals("matrikulant")){
            payFromWallet(
                    PaymentRequestDTO.builder()
                            .amount(30)
                            .studentCode(currentCode)
                            .build()
            );
            sendExamRegistration(examToRegister);
        }

        //tu
        String resourceUrl2
                = "http://localhost:8080/api/subject/subjects";

        URL url2 = new URL(resourceUrl2);
        URLConnection request2 = url2.openConnection();
        request2.connect();


        JsonParser jp2 = new JsonParser();
        JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request2.getContent()));
        JsonArray rootobjArray2 = root2.getAsJsonArray();

        String subName = new String();

        for(int i=0;i<rootobjArray2.size();i++){
            JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
            String name1 = rootobj.get("name").getAsString();
            Long id = rootobj.get("id").getAsLong();
            if(examRegisterDTO.getStudentCode().equals(name1))
                subName = id.toString();
        }
        //tu
        return "redirect:/examSubject/"+subName;
    }

    private String getUserStatusFromCode(String userCode) throws JsonProcessingException {

        StudentRequestDTO studentRequestDTO = StudentRequestDTO
                .builder()
                .studentCode(userCode)
                .build();

        String resourceUrl
                = "http://localhost:8080/api/student/fullstudentinfo";

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(studentRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

        String studentJson = response.getBody();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(studentJson);
        JsonObject rootObj = root.getAsJsonObject();

        String userStatus = rootObj.get("status").getAsString();

        return userStatus;
    }

    private void sendExamRegistration(ExamRegisterDTO examRegisterDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/exam/registerExam";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(examRegisterDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    @RequestMapping("/wallet/{userCode}")
    public String showWallet(Model model, @PathVariable(name = "userCode") String userCode) throws Exception{
        if(isAuthenticated()) {
            StudentRequestDTO studentRequestDTO = StudentRequestDTO
                    .builder()
                    .studentCode(userCode)
                    .build();

            String resourceUrl
                    = "http://localhost:8080/api/wallet/status";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(studentRequestDTO);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String studentJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(studentJson);
            JsonObject rootObj = root.getAsJsonObject();


            WalletResponseDTO walletResponseDTO = WalletResponseDTO
                    .builder()
                    .status(rootObj.get("status").getAsInt())
                    .build();

            //tu novo
            String resourceUrl1
                    = "http://localhost:8080/api/student/fullstudentinfo";

            RestTemplate restTemplate1 = new RestTemplate();
            ObjectMapper mapper1 = new ObjectMapper();
            String jsonString1 = mapper1.writeValueAsString(studentRequestDTO);
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity1 = new HttpEntity<String>(jsonString1, headers1);
            ResponseEntity<String> response1 = restTemplate1.postForEntity(resourceUrl1, entity1, String.class);

            String studentJson1 = response1.getBody();

            JsonParser jp1 = new JsonParser();
            JsonElement root1 = jp1.parse(studentJson1);
            JsonObject rootObj1 = root1.getAsJsonObject();


            StudentResponseFullDTO studentResponseDTO = StudentResponseFullDTO
                    .builder()
                    .identificationNumber(rootObj1.get("identificationNumber").getAsString())
                    .studentCode(rootObj1.get("studentCode").getAsString())
                    .firstName(rootObj1.get("firstName").getAsString())
                    .lastName(rootObj1.get("lastName").getAsString())
                    .birthDate(rootObj1.get("birthDate").getAsString())
                    .year(rootObj1.get("year").getAsInt())
                    .department(rootObj1.get("department").getAsString())
                    .major(rootObj1.get("major").getAsString())
                    .status(rootObj1.get("status").getAsString())
                    .build();

            model.addAttribute("student", studentResponseDTO);
            model.addAttribute("walletStatus", walletResponseDTO);

            return "studentWallet";
        }
        else return "redirect:/";
    }

    @PostMapping("/wallet_pay")
    public String payWallet(PaymentRequestDTO paymentRequestDTO) throws JsonProcessingException {

        PaymentRequestDTO payment = PaymentRequestDTO
                .builder()
                .amount(paymentRequestDTO.getAmount())
                .studentCode(paymentRequestDTO.getStudentCode())
                .build();

        payFromWallet(payment);

        return "redirect:/wallet/"+paymentRequestDTO.getStudentCode();
    }

    @PostMapping("/wallet_add")
    public String addWallet(PaymentRequestDTO paymentRequestDTO) throws JsonProcessingException {

        PaymentRequestDTO payment = PaymentRequestDTO
                .builder()
                .amount(paymentRequestDTO.getAmount())
                .studentCode(paymentRequestDTO.getStudentCode())
                .build();

        addToWallet(payment);

        return "redirect:/wallet/"+paymentRequestDTO.getStudentCode();
    }

    private void payFromWallet(PaymentRequestDTO paymentRequestDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/wallet/pay";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(paymentRequestDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    private void addToWallet(PaymentRequestDTO paymentRequestDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/wallet/add";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(paymentRequestDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    //ovo radi - tu sam stao

    @PostMapping("/exam_students")
    public String studentsExam(ExamIdsDTO examIdsDTO) throws Exception {

        //tu
        return "redirect:/studentsExam/"+examIdsDTO.getIdExam();
    }

    //ovdje cemo  dobiti student exam podatke i onda cemo svaki kao formu za ocjeniti, trebace nam i list usera sa frontenda
    @RequestMapping("/studentsExam/{idExam}")
    public String showStudentsExam(Model model, @PathVariable(name = "idExam") String idExam) throws Exception {

        if(isAuthenticated()) {
            OnlyIDsDTO examIdsDTO = OnlyIDsDTO.builder()
                    .id(Integer.parseInt(idExam))
                    .build();

            String resourceUrl
                    = "http://localhost:8080/api/exam/studentsExam";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(examIdsDTO);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String studentJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(studentJson);
            JsonArray rootobjArray = root.getAsJsonArray();

            List<ExamStudentsResponseDTO> listExams = new ArrayList<ExamStudentsResponseDTO>();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootObj = rootobjArray.get(i).getAsJsonObject();
                listExams.add(
                        ExamStudentsResponseDTO
                        .builder()
                        .studentCode(rootObj.get("studentCode").getAsString())
                        .complete(rootObj.get("complete").getAsBoolean())
                        .grade(rootObj.get("grade").getAsInt())
                        .score(rootObj.get("score").getAsInt())
                        .id(rootObj.get("id").getAsLong())
                        .build()
                );
            }
            //


            List<User> listStudents = userRepository.findAllStudents();

            //System.out.println(studentJson);

            model.addAttribute("redirect", idExam);
            model.addAttribute("examInfo", listExams);
            model.addAttribute("listStudents", listStudents);
            return "examInfo";
        }
        else return "redirect:/";

    }

    //ocjenjivanje
    @PostMapping("/grade_exam")
    public String gradeExam(ExamGradeRedirDTO examGradeDTO) throws JsonProcessingException {

        ExamGradeDTO examGrade = ExamGradeDTO.builder()
                .id(examGradeDTO.getId())
                .grade(examGradeDTO.getGrade())
                .score(examGradeDTO.getScore())
                .complete(true)
                .build();


        sendExamGrade(examGrade);

        return "redirect:/studentsExam/"+examGradeDTO.getRedirect();
    }

    private void sendExamGrade(ExamGradeDTO examGradeDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/exam/gradeExam";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(examGradeDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    // tu radimo
    @RequestMapping("/subjectStudents/{id}")
    public String showStudentsForSubject(Model model, @PathVariable(name = "id") String id) throws Exception{
        if(isAuthenticated()) {
            String resourceUrl2
                    = "http://localhost:8080/api/subject/subjects";

            URL url2 = new URL(resourceUrl2);
            URLConnection request2 = url2.openConnection();
            request2.connect();


            JsonParser jp2 = new JsonParser();
            JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request2.getContent()));
            JsonArray rootobjArray2 = root2.getAsJsonArray();

            String subName = new String();

            for(int i=0;i<rootobjArray2.size();i++){
                JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
                String name1 = rootobj.get("name").getAsString();
                Long idq = rootobj.get("id").getAsLong();
                if(id.equals(idq.toString()))
                    subName = name1;
            }
            //dddd

            String resourceUrl
                    = "http://localhost:8080/api/subject/getAllStudentSubjects";

            URL url = new URL(resourceUrl);
            URLConnection request = url.openConnection();
            request.connect();

            List<StudentSubjectSpecialDTO> listStudentSubjects = new ArrayList<StudentSubjectSpecialDTO>();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray rootobjArray = root.getAsJsonArray();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootobj = rootobjArray.get(i).getAsJsonObject();
                if(rootobj.get("subject").getAsString().equals(subName)){
                    listStudentSubjects.add(
                            StudentSubjectSpecialDTO
                                    .builder()
                                    .id(rootobj.get("id").getAsLong())
                                    .studentCode(rootobj.get("studentCode").getAsString())
                                    .subject(rootobj.get("subject").getAsString())
                                    .score(rootobj.get("score").getAsInt())
                                    .grade(rootobj.get("grade").getAsInt())
                                    .date(rootobj.get("date").getAsString())
                                    .complete(rootobj.get("complete").getAsBoolean())
                                    .build()
                    );
                }

            }

            List<User> listStudents = userRepository.findAllStudents();

            model.addAttribute("redir", id);
            model.addAttribute("listStudents", listStudents);
            model.addAttribute("listStudentSubjects", listStudentSubjects);

            return "subjectStudent";
        }
        else return "redirect:/";
    }

    //sad tu
    @PostMapping("/process_subject_student")
    public String gradeStudentSubject(StudentSubjectSpecialDTO studentSubjectSpecialDTO) throws JsonProcessingException {

        StudentSubjectDTO studentSubjectDTO = StudentSubjectDTO.builder()
                .studentCode(studentSubjectSpecialDTO.getStudentCode())
                .grade(studentSubjectSpecialDTO.getGrade())
                .score(studentSubjectSpecialDTO.getScore())
                .subject(studentSubjectSpecialDTO.getSubject())
                .date(studentSubjectSpecialDTO.getDate())
                .complete(true)
                .build();


        sendStudentSubjectGrade(studentSubjectDTO);

        return "redirect:/subjectStudents/"+studentSubjectSpecialDTO.getId();
    }

    private void sendStudentSubjectGrade(StudentSubjectDTO studentSubjectDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "http://localhost:8080/api/subject/addStudentToSubject";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(studentSubjectDTO);
        //System.out.println(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);
    }

    //gore sve radi

    @RequestMapping("/allExamsForStudent/")
    public String showExamsForStudent(Model model) throws Exception{
        if(isAuthenticated()) {
            CustomUserDetails userSecurity = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentCode = userSecurity.getUserCode();

            StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                    .studentCode(currentCode)
                    .build();

            String resourceUrl
                    = "http://localhost:8080/api/exam/examsForStudent";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(studentRequestDTO);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, entity, String.class);

            String studentJson = response.getBody();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(studentJson);
            JsonArray rootobjArray = root.getAsJsonArray();

            List<ExamStudentsResponseDTO> listExams = new ArrayList<ExamStudentsResponseDTO>();

            for(int i=0;i<rootobjArray.size();i++){
                JsonObject rootObj = rootobjArray.get(i).getAsJsonObject();
                listExams.add(
                        ExamStudentsResponseDTO
                                .builder()
                                .studentCode(rootObj.get("studentCode").getAsString())
                                .complete(rootObj.get("complete").getAsBoolean())
                                .grade(rootObj.get("grade").getAsInt())
                                .score(rootObj.get("score").getAsInt())
                                .id(rootObj.get("id").getAsLong())
                                .build()
                );
            }
            //

            String resourceUrl2
                    = "http://localhost:8080/api/exam/examsAll";

            URL url2 = new URL(resourceUrl2);
            URLConnection request2 = url2.openConnection();
            request2.connect();

            List<ExamResponseDTO> listActualExams = new ArrayList<ExamResponseDTO>();

            JsonParser jp2 = new JsonParser();
            JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request2.getContent()));
            JsonArray rootobjArray2 = root2.getAsJsonArray();

            for(int i=0;i<rootobjArray2.size();i++){
                JsonObject rootobj = rootobjArray2.get(i).getAsJsonObject();
                listActualExams.add(
                        ExamResponseDTO
                                .builder()
                                .date(rootobj.get("date").getAsString())
                                .id(rootobj.get("id").getAsLong())
                                .subject(rootobj.get("subject").getAsString())
                                .build()
                );
            }

            model.addAttribute("listExams", listActualExams);


            //System.out.println(studentJson);

            model.addAttribute("examInfo", listExams);

            return "examInfoStudent";
        }
        else return "redirect:/";
    }


}
