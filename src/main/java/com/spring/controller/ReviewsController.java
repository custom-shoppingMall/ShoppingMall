package com.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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

import com.spring.dto.ReviewVO;
import com.spring.function.FunctionSpring;
import com.spring.service.ReviewServiceImpl;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReviewsController {
	private static String URL_PATH="http://pvpvpvpvp.gonetis.com:8080/sample/com/review-image/";
	private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);
	private static String SAVE_PATH="c:/Users/kim/Desktop/project/ShoppingMall/src/main/java/com/image/review/";
	
	@Inject
	private ReviewServiceImpl reService;
	
	// 리뷰?�� ?���?�? 반환 ?��?�� API
	@CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value="/reviews",method = RequestMethod.GET,produces = "application/json; charset=utf8")
    @ResponseBody
    public String reviewList()
    {
		System.out.println("reviews.GET");
		JSONObject jsonObject = new JSONObject();
    	JSONArray jsonArarry = new JSONArray();
    	List<ReviewVO> sql = reService.selectList();
    	
    	for(int i=0;i<sql.size();i++)
    	{
    		String[] image = sql.get(i).getImage().split(",");    
			JSONObject list = new JSONObject();
			JSONObject imgJ = new JSONObject();
			JSONArray jsonimg = new JSONArray();
			list.put("index", i);
			list.put("content", sql.get(i).getContent());
			list.put("title", sql.get(i).getTitle());
			//Date ?��?��?? ?��?���? ?��?��?�� ?��?��?�� 문제�? ?���? �?경해?�� ?��?���?
//			list.put("indate", sql.get(i).getInDate());
			for(String img:image)
			{
				if(img!="") 
					jsonimg.add(URL_PATH+img);
			}
			
			list.put("hit", sql.get(i).getHit());
			list.put("userId", sql.get(i).getId());
			imgJ.put("image", jsonimg);
			list.put("images", imgJ);
			list.put("userName",sql.get(i).getName());
			list.put("product", sql.get(i).getProduct());
			list.put("reviewsNumber", sql.get(i).getReviewsNumber());
			jsonArarry.add(list);
			jsonObject.put("reviews", jsonArarry);
			System.out.println(jsonArarry.toString());
    	}
    	return jsonObject.toString();
    }
	// 리뷰�? ?��?��?��?�� API
	@CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/reviews",method = RequestMethod.POST,produces = "application/json; charset=utf8"
  		  )
    @ResponseBody
    public String reviewInsert(
    		@RequestParam("content") String content,
    		@RequestParam("title") String title,
    		@RequestParam("image") List<MultipartFile> imageReview,
    		@RequestParam("productNumber") Long productNumber
    	) throws IOException
    {
		System.out.println(content);
		System.out.println(imageReview);
		//content title image indate hit(0), usersNumber, productsNumber
		// hit?�� 기본�? 0 userNumber?�� 로그?��?�� ?��료되�? ?��?��?�� ?��?���??�� ?��?��.
		ReviewVO vo = new ReviewVO(content, title, 
				FunctionSpring.fileSave(imageReview,SAVE_PATH),
				new Date(), (long)0, (long)1, productNumber);
		JSONObject json = new JSONObject();
		boolean insert = reService.insertReview(vo);
		String result = (insert==true)?"insert":"fail";
		json.put("result",result);
		return json.toString();
    }
	
	// 리뷰?�� ?��?��?��?�� ?��?�� API
	// 기존?�� ?��?��?�� �??��?�� ?��?��?�� ?��?��?��?��?��?��.!
	@CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/reviews/{reviewsNumber}",method = RequestMethod.POST,produces = "application/json; charset=utf8"
  		  )
    @ResponseBody
    public String reviewUpdate(
    		@PathVariable("reviewsNumber") String reviewsNumber,
    		@RequestParam("content") String content,
    		@RequestParam("title") String title,
    		@RequestParam("image") List<MultipartFile> imageReview	
    	) throws IOException
    {
		JSONObject json = new JSONObject();
		ReviewVO vo = new ReviewVO();
		vo.setContent(content);
		vo.setTitle(title);
		vo.setImage(FunctionSpring.fileSave(imageReview, SAVE_PATH));
		vo.setReviewsNumber(Long.valueOf(reviewsNumber));
		boolean update = reService.updateReview(vo);
		String result = (update==true)?"update":"fail";
		json.put("result",result);
		return json.toString();
	
    }
	// 리뷰?�� ?��?��?��?�� API
	// ?��?��?�� 같이 ?��?��?���??��.!
	@CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/reviews/{reviewsNumber}",method = RequestMethod.DELETE,produces = "application/json; charset=utf8"
  		  )
    @ResponseBody
    public String reviewUpdate(
    		@PathVariable("reviewsNumber") String reviewsNumber)
    {
		JSONObject json = new JSONObject();
		ReviewVO vo = new ReviewVO();
		try { //?��?�� ?�� ?��?��?�� ???��?�� ?��?��?��?��
			vo.setReviewsNumber(Long.valueOf(reviewsNumber));
    	}catch(Exception e)
    	{
    		json.put("error", e);
    		return json.toString();
    	}
		String imgurl= reService.selectImage(vo);
		boolean delete = reService.deleteReview(vo);
		if(delete)
		{
			boolean fdelete = FunctionSpring.fileDelete(imgurl, SAVE_PATH);
			System.out.println("delete File?:"+fdelete);
		}
		String result = (delete==true)?"delete":"fail";
		json.put("result",result);
		return json.toString();
		
    }
	
	//reviewNumber?��?��?�� ?��?��?��?��?��?�� ?��?��?��?��?��?��?�� ?��?��몌옙?��?��?��
	@CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/com/review/{reviewNumber}",method = RequestMethod.GET
  		  )
    @ResponseBody 
    public JSONObject reviewContent(@PathVariable("reviewNumber") String reviewNumber)
    {
	 	return null;
    }
	
	// 리뷰?�� 좋아?�� ?��리는 API
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/reviewsqweqwe",method = RequestMethod.GET
  		  )
    @ResponseBody 
    public String reviewsLike()
    {
	 	return null;
    }
	
	
	// 리뷰?�� ?��미�?�? 보내주는 API
    @CrossOrigin(origins = "*", allowedHeaders = "*")  
    @RequestMapping(
  		  value = "/com/review-image/{img}",method = RequestMethod.GET
  		 ,produces = MediaType.IMAGE_JPEG_VALUE
  		  )
    @ResponseBody 
	public byte[] getImageWithMediaType(@PathVariable("img") String img) throws IOException {
    	String url = "/com/image/review/"+img+".png";
    	System.out.println(url);
	    InputStream in = getClass().getResourceAsStream(url);
	    System.out.println(img+".png");
	    return IOUtils.toByteArray(in);
	}
	
	
}


