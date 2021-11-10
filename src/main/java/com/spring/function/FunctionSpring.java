package com.spring.function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FunctionSpring {
	// ������ �������ִ� �Լ� 
    // ���� ������ SAVE_PATH��ġ�� ������ �̸����� �����ϰ�
    // ����� �̸��� �����Ѵ�.
    // TODO: �ߺ��� �ƿ� �Ͼ �� ���� ����ǰų� �ߺ��� �ٽ� �õ� �� �� �ְ� ¥����(���)
    public static String fileSave(List<MultipartFile> files ,String SAVE_PATH) throws IOException
    {
    	FileOutputStream fos = null;
    	System.out.println(files.size());
    	String imageurl = "";
    	for(int i=0;i<files.size();i++)
    	{
			byte[] fileData = files.get(i).getBytes();
			int ramdomCount= (int)(Math.random()*System.currentTimeMillis()%10000000);
			try {
			fos = new FileOutputStream(SAVE_PATH+"test"+ramdomCount+".png");
			imageurl+="test"+ramdomCount+",";
			}
			catch (Exception e) {
				fos = new FileOutputStream(SAVE_PATH+"test1"+ramdomCount+".png");
				imageurl+="test1"+ramdomCount+",";
			}
			fos.write(fileData);
			
    	}
    	fos.close();
    	return imageurl;
    }
    public static boolean fileDelete(String imageName,String SAVE_PATH)
    {
    	String[] imageUrl = imageName.split(",");
    	boolean result = false;
    	for(String url:imageUrl)
    	{
    		if(url!=null)
    		{
    			File img = new File(SAVE_PATH+url+".png");
	    		result = img.delete();
    		}
    	}
    	return result;
    }
}
