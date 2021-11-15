package com.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dto.ProductVO;
import com.spring.function.FunctionSpring;
import com.spring.service.ProductsServiceImpl;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductsController {
	private static String URL_PATH="http://pvpvpvpvp.gonetis.com:8080/sample/com/product-image/";
//	private static String URL_PATH="http://pvpvpvpvp.gonetis.com:8080/sample";
	private static String SAVE_PATH="c:/Users/kim/Desktop/project/ShoppingMall/src/main/java/com/image/";
    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);
    
    //?���? ?��?��?�� ?��?? API ?��?��?�� ?��?�� �? �??�� 목적?? ?��?��블에 �??��?�� ?��?���? select 쿼리?�� ?��?��?��?��.
    private static boolean ProductsChange = true;
    private static List<ProductVO> sql=null;
    private static JSONObject JSONObPro = new JSONObject();
    private static Map<Long,ProductVO> mapSql = new HashMap<Long,ProductVO>();
    @Inject
    private ProductsServiceImpl proService;
    
    
    // ?��?��?�� 모두 보내주는 API
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value="/products",method = RequestMethod.GET,produces = "application/json; charset=utf8")
    @ResponseBody
    public String productsList(
    		@RequestParam(value="kind",required=false) String kindP,
    		@RequestParam(value="color",required=false) String colorP,
    		@RequestParam(value="size",required=false) String sizeP,
    		@RequestParam(value="price",required=false) String priceP
    		) throws IOException {   	
    	if(!ProductsChange)
    	{
    		return JSONObPro.toString();
    	}
		sql = proService.selectList();
		ProductsChange=!ProductsChange;
		JSONArray jsonArarry = new JSONArray();
	
    	System.out.println("kind:"+kindP);
    	System.out.println("color:"+colorP);
    	System.out.println("size:"+sizeP);
    	System.out.println("price:"+priceP);
    	
    	for(int i=0;i<sql.size();i++)
    	{
    		if((kindP==null||kindP.equals(sql.get(i).getKind())))
    		{
//    			System.out.println(sql.get(i).getKind());
				JSONObject list = new JSONObject();
				JSONObject colorJ = new JSONObject();
				JSONArray jsoncolors = new JSONArray();
				// ?��미�??�� ?��?���? 존재 ?�� ?�� ?���? ?��문에 ,�? 구분?��?�� ?��?�� ,�? SPLIT
				String[] image = sql.get(i).getImageSmall().split(",");    
				// ?��?��?��?�� ?��?�� ?��?��?�� 추기
				list.put("index", i);
				// ?��?��코드?�� ?��?���? 존재 ?�� ?�� ?���? ?��문에 #�? 구분?��?�� ?��?�� #�? SPLIT
				String colors[] = sql.get(i).getColor().split("#");
				// colors 배열?�� ???��
				for(String color:colors)
				{
					if(color!="") { //초기?�� ?��기는 빈배?��?�� ?��?��?���? �? ?��목에 #추�?
						jsoncolors.add("#"+color);						
					}
				}
				colorJ.put("color",jsoncolors);
				list.put("colors", colorJ);					
				list.put("kind", sql.get(i).getKind());
				list.put("size", sql.get(i).getSize());
				list.put("price", sql.get(i).getPrice());
				list.put("product", sql.get(i).getProduct());
				list.put("productNumber", sql.get(i).getProductNumber());			
				list.put("image", URL_PATH+image[0]);
				jsonArarry.add(list);
				JSONObPro.put("products", jsonArarry); 	
    		}
    	}
	
    	
    	return JSONObPro.toString();
    }
    
    // ?��?��?�� ?��록하?�� API
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value="/products",method = RequestMethod.POST,produces = "application/json; charset=utf8")
    @ResponseBody
    public String productsInsertOrUpdate(
    		@RequestParam("size") String size,
    		@RequestParam("color") String color,
    		@RequestParam("kind") String kind,
    		@RequestParam("quantity") String quantity,
    		@RequestParam("content") String content,
    		@RequestParam("imageSmall") List<MultipartFile> imageSmall,
    		@RequestParam("imageLazy") List<MultipartFile> imageLazy,
    		@RequestParam("productImage") List<MultipartFile> productImage,
    		@RequestParam("product") String product,
    		@RequestParam("price") Long price
    		) throws IOException
    {
    	
    	// ?�� 구간?�� 최소?�� ?��?�� ..
    	ProductVO vo = new ProductVO();
    	vo.setProduct(product);
    	System.out.println(vo.toString());
    	
    	Long productNumber = proService.selectCheckInsert(vo);
    	System.out.println(productNumber);
    	vo = new ProductVO(size, color, kind, quantity, price, content, product, productNumber);
    	JSONObject json = new JSONObject();
    	String result ="fail";
    	if(productNumber!=0)// ?��?��번호 존재 ?��무에 ?��?��?�� UPDATE INSERT �? ?��?��?�� ?��?��?��?��.
    	{
    		vo.setRegDate(new Date());
    		vo.setImageSmall(FunctionSpring.fileSave(imageSmall,SAVE_PATH));
    		vo.setImageLazy(FunctionSpring.fileSave(imageLazy,SAVE_PATH));
    		vo.setProductImage(FunctionSpring.fileSave(productImage,SAVE_PATH));
    		boolean sqlUpdate = proService.updateProduct(vo);
    		result = (sqlUpdate==true)?"update":"fail";
    		json.put("result", result);
    		
    	}
    	if(productNumber==0)
    	{
    		vo.setRegDate(new Date());
    		vo.setImageSmall(FunctionSpring.fileSave(imageSmall,SAVE_PATH));
    		vo.setImageLazy(FunctionSpring.fileSave(imageLazy,SAVE_PATH));
    		vo.setProductImage(FunctionSpring.fileSave(productImage,SAVE_PATH));
    		//?��?��번호�? ???��?��?��건데 ?���? ?��?��?��?��?��?
//    		vo.setProductNumber((long)(Math.random()*System.currentTimeMillis())%10000000);
    		boolean sqlInsert = proService.insertProduct(vo);
    		result = (sqlInsert==true)?"insert":"fail";
    		json.put("result", result);
    	}
    	if(!result.equals("fail"))
    		ProductsChange=false;
    	// ?��?��?��?�� 발생?�� ?��?�� ?��?��?���? ???��?�� ?��값을 초기?��
    	if(result.equals("update"))
    		mapSql.remove(productNumber);
    	return json.toString();
    	
    }
    
    // ?��?��?�� ?���??��보�?? �??��?��?�� API
    @CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/products/{productNumber}",method = RequestMethod.GET,produces = "application/json; charset=utf8"
    		)
    @ResponseBody 
    public String productCotent(@PathVariable("productNumber") String productNumber) {
    	
    	
    	JSONObject jsonObject= new JSONObject();
    	ProductVO vo = new ProductVO();
    	
    	try { //?��?�� ?�� ?��?��?�� ???��?�� ?��?��?��?��
    		vo.setProductNumber(Long.valueOf(productNumber));
    		mapSql.put(Long.valueOf(productNumber), vo);
    	}catch(Exception e)
    	{
    		jsonObject.put("error", e);
    		return jsonObject.toString();
    	}
    	
    	vo = proService.selectProduct(vo);
    	
    	jsonObject.put("product", vo.getProduct());
    	jsonObject.put("sizes",FunctionSpring.sizeArray(vo.getSize()));
//    	jsonObject.put("size", vo.getSize());
    	JSONObject colorJ = new JSONObject();
		JSONArray jsoncolors = new JSONArray();
    	String colors[] = vo.getColor().split("#");
		// colors�� size��ŭ �ݺ�
		for(String color:colors)
		{
			if(color!="") //0�� �ε����� ���??�� �������� 
				jsoncolors.add("#"+color);
		}
		jsonObject.put("id",vo.getProductNumber());
		colorJ.put("color",jsoncolors);
		jsonObject.put("colors", colorJ);
    	jsonObject.put("kind", vo.getKind());
    	jsonObject.put("quantity", vo.getQuantity());
    	jsonObject.put("price", vo.getPrice());
    	jsonObject.put("content", vo.getContent());
//    	jsonObject.put("image", vo.getProduct());
    	String[] image = vo.getImageSmall().split(","); 
    	JSONObject imagelist = new JSONObject();
    	JSONArray imageArr = new JSONArray();
    	for(int i=0;i<image.length;i++)
    	{   		
    		imageArr.add(i, URL_PATH+image[i]);	
    	}
    	jsonObject.put("image", imageArr);
    	return jsonObject.toString();
    }
   
    // ?��?��?�� ?��미�?�? 보내주는 API
    @CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/com/product-image/{img}",method = RequestMethod.GET
  		 ,produces = MediaType.IMAGE_JPEG_VALUE
  		  )
    @ResponseBody 
	public byte[] getImageWithMediaType(@PathVariable("img") String img) throws IOException {
    	String url = "/com/image/"+img+".png";
//    	System.out.println(url);
	    InputStream in = getClass().getResourceAsStream(url);
//	    System.out.println(img+".png");
	    return IOUtils.toByteArray(in);
	}
   
}
