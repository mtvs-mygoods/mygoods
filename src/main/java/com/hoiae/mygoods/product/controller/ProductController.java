package com.hoiae.mygoods.product.controller;
import com.hoiae.mygoods.product.dto.CharacterDTO;
import com.hoiae.mygoods.product.dto.ProductDTO;
import com.hoiae.mygoods.product.service.ProductService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

//    @Value("${image.image-dir}")
//    private String IMAGE_DIR;

    @Value("${image.image-dir}")
    private String IMAGE_DIR;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public  String product() {
        return "content/product/index";
    }


    @ResponseBody
    @PostMapping("/character")
    public String uploadCharacter(@RequestParam("image") MultipartFile image) throws ParseException, IOException {


        System.out.println("image : " + image.getOriginalFilename());

        String rootLocation = IMAGE_DIR;

        String fileUploadDirectory = rootLocation + "/upload/original";
        File directory = new File(fileUploadDirectory);

        if(!directory.exists()){
            directory.mkdirs();
        }

        List<Map<String, String>> fileList = new ArrayList<>();

        List<MultipartFile> paramFileList = new ArrayList<>();
        paramFileList.add(image);

        try {
            for (MultipartFile paramFile : paramFileList) {
                if (paramFile.getSize() > 0) {
                    String originFileName = paramFile.getOriginalFilename();

                    String ext = originFileName.substring(originFileName.lastIndexOf("."));
                    String savedFileName = "(c)" + UUID.randomUUID().toString().replace("-", "") + ext;


                    paramFile.transferTo(new File(fileUploadDirectory + "/" + savedFileName));

                    /* DB??? ???????????? ????????? ????????? ???????????? ???????????? ?????? ?????? */
                    /* ????????? ????????? Map??? ?????????. */
                    Map<String, String> fileMap = new HashMap<>();
                    fileMap.put("originFileName", originFileName);
                    fileMap.put("savedFileName", savedFileName);
                    fileMap.put("savePath", fileUploadDirectory);

                    System.out.println("???????????? " +   fileUploadDirectory + "/" + savedFileName);


                    CharacterDTO character  = new CharacterDTO();
                    character.setMemberNo(1);
                    character.setModelName("????????? Hosoda ");
                    character.setCharacterImageUrl(savedFileName);

                    productService.registCharacter(character);

                }
            }

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();

            /* ?????? ????????? Exception??? ?????? ?????????????????? ??? ????????? ???????????? ??????. */
            int cnt = 0;
            for (int i = 0; i < fileList.size(); i++) {
                Map<String, String> file = fileList.get(i);

                File deleteFile = new File(fileUploadDirectory + "/" + file.get("savedFileName"));
                boolean isDeleted1 = deleteFile.delete();

                if (isDeleted1) {
                    cnt++;
                }
            }

            if (cnt == fileList.size()) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }

        //RestTemplate??? ????????? ?????? ?????? ?????????

        /*  create RestTemplate instance*/
        RestTemplate restTemplate = new RestTemplate();

        /*request header*/
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        /*request body*/
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        System.out.println(image.getResource());
        body.add("file", image.getResource());

        /*????????? ?????? ????????? ????????? HttpEntity ??????????????? ???????????? RestTemplate??? ???????????? ????????????.*/
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        /*restTemplate.postForEntity() ????????? ????????? URL??? ???????????? ????????? ????????? ????????? ????????? ??????*/

        String url  = "https://7d663fa4-f5b9-47a2-98b1-7cbe746422fd.mock.pstmn.io/product";


        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject) obj;
        System.out.println("json['result'] : " + jsonObj.get("result"));
        System.out.println("json['img'] : " + jsonObj.get("img"));

        String changeImg = (String) jsonObj.get("img");
        byte[] binary = Base64.getDecoder().decode(changeImg);
        System.out.println("binary" + binary);

        String savedFileName = "(c)" + UUID.randomUUID().toString().replace("-", "");
//        Files.write(Paths.get("D:\\mygoods\\mygoods\\src\\main\\resources\\upload\\change\\"+savedFileName+".jpg"), binary);
        Files.write(Paths.get(rootLocation+"\\upload\\change\\"+savedFileName+".jpg"), binary);

        return response.getBody();
    }

    @ResponseBody
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("image") MultipartFile userImage,
                              @RequestParam("categoryCode") String categoryCode,
                              @RequestParam("modelName") String modelName,
                              HttpServletRequest request
    ) throws IOException, ParseException {

        /*???????????????, ??????????????????, ???????????? ??????*/
        System.out.println("file : " + userImage.getOriginalFilename());
        System.out.println("categoryCode : " + categoryCode);
        System.out.println("modelName : " + modelName);

        /* ???????????????*/
        String rootLocation = IMAGE_DIR;

        String fileUploadDirectory = rootLocation + "\\upload\\original";
        System.out.println("test!");
        System.out.println(fileUploadDirectory);

        File directory = new File(fileUploadDirectory);

        if(!directory.exists()){
            directory.mkdirs();
        }

        List<Map<String, String>> fileList = new ArrayList<>();

        List<MultipartFile> paramFileList = new ArrayList<>();
        paramFileList.add(userImage);

        try {
            for (MultipartFile paramFile : paramFileList) {
                if (paramFile.getSize() > 0) {
                    String originFileName = paramFile.getOriginalFilename();

                    String ext = originFileName.substring(originFileName.lastIndexOf("."));
                    String savedFileName = "(p)" + UUID.randomUUID().toString().replace("-", "") + ext;


                    paramFile.transferTo(new File(fileUploadDirectory + "\\" + savedFileName));

                    /* DB??? ???????????? ????????? ????????? ???????????? ???????????? ?????? ?????? */
                    /* ????????? ????????? Map??? ?????????. */
                    Map<String, String> fileMap = new HashMap<>();
                    fileMap.put("originFileName", originFileName);
                    fileMap.put("savedFileName", savedFileName);
                    fileMap.put("savePath", fileUploadDirectory);

                    System.out.println("???????????? " +   fileUploadDirectory + "/" + savedFileName);

                    ProductDTO product = new ProductDTO();
                    product.setProductName("????????? ?????????");
                    product.setProductPrice(10000);
                    product.setCategoryCode(3);
                    product.setCharachterCode(1);
                    product.setProductImageUrl(savedFileName);

                    productService.registProduct(product);

                }
            }

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();

            /* ?????? ????????? Exception??? ?????? ?????????????????? ??? ????????? ???????????? ??????. */
            int cnt = 0;
            for (int i = 0; i < fileList.size(); i++) {
                Map<String, String> file = fileList.get(i);

                File deleteFile = new File(fileUploadDirectory + "/" + file.get("savedFileName"));
                boolean isDeleted1 = deleteFile.delete();



                if (isDeleted1) {
                    cnt++;
                }
            }

            if (cnt == fileList.size()) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }


        //RestTemplate??? ????????? ?????? ?????? ?????????

        /*  create RestTemplate instance*/
        RestTemplate restTemplate = new RestTemplate();

        /*request header*/
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        /*request body*/
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        System.out.println(userImage.getResource());
        body.add("file", userImage.getResource());
        body.add("categoryCode",categoryCode);


        /*????????? ?????? ????????? ????????? HttpEntity ??????????????? ???????????? RestTemplate??? ???????????? ????????????.*/
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        /*restTemplate.postForEntity() ????????? ????????? URL??? ???????????? ????????? ????????? ????????? ????????? ??????*/

        String url  = "https://7d663fa4-f5b9-47a2-98b1-7cbe746422fd.mock.pstmn.io/product";


        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        //tshirt, plate,ecobag


        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject) obj;
        System.out.println("json['result'] : " + jsonObj.get("result"));
        System.out.println("json['img'] : " + jsonObj.get("img"));



        return response.getBody();

    }
}