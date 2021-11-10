package com.yjxxt.manager.controller;


import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.yjxxt.common.result.BaseResult;
import com.yjxxt.common.result.FileResult;
import com.yjxxt.manager.pojo.Goods;
import com.yjxxt.manager.pojo.GoodsImages;
import com.yjxxt.manager.query.GoodsQuery;
import com.yjxxt.manager.service.IBrandService;
import com.yjxxt.manager.service.IGoodsCategoryService;
import com.yjxxt.manager.service.IGoodsImageService;
import com.yjxxt.manager.service.IGoodsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("goods")
public class GoodsController {

    @Resource
    private IGoodsCategoryService goodsCategoryService;


    @Resource
    private IBrandService brandService;


    @Resource
    private IGoodsService goodsService;





    @Value("${qiniu.ak}")
    private String ak;
    @Value("${qiniu.sk}")
    private String sk;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("${qiniu.domain}")
    private String domain;


    @Resource
    private IGoodsImageService goodsImageService;



    @RequestMapping("/list/page")
    public String index(Model model){
        model.addAttribute("gcList",goodsCategoryService.queryAllGoodsCategoriesForGoodsCategory());
        model.addAttribute("brList",brandService.queryAllBrands());
        return "goods/goods_list";
    }

    /**
     * 商品添加页面
     * @return
     */
    @RequestMapping("/add/page")
    public String addPage(Model model){
        model.addAttribute("goodsCategoriesList",goodsCategoryService.queryGoodsCategoriesByParentId((short)0));
        model.addAttribute("brandList",brandService.queryAllBrands());
        return "goods/goods_add";
    }


    @RequestMapping("saveGoods")
    @ResponseBody
    public BaseResult saveGoods(Goods goods){
        try {
            goodsService.saveGoods(goods);
            return  BaseResult.success(goods.getGoodsId());
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.error(e.getMessage());
        }
    }


    @RequestMapping("image/save")
    @ResponseBody
    public BaseResult imageSave(@RequestParam(name = "file") MultipartFile multipartFile,Integer goodsId){
        FileResult fileResult = new FileResult();
        if (null == multipartFile || multipartFile.isEmpty()) {
            return BaseResult.error("文件内容不能为空!");
        }
        try {
            InputStream is = multipartFile.getInputStream();
            //构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.region0());
            //...其他参数参考类注释
            UploadManager uploadManager = new UploadManager(cfg);
            String fileName = new SimpleDateFormat("yyyy/MM/dd/").format(new Date())
                    + System.currentTimeMillis() + multipartFile.getOriginalFilename()
                    .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            Auth auth = Auth.create(ak, sk);
            String upToken = auth.uploadToken(bucket);
            Response response = uploadManager.put(is, fileName, upToken,null,null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            fileResult.setSuccess("文件上传成功!");
            fileResult.setFileUrl(domain+putRet.key);
            GoodsImages goodsImages=new GoodsImages();
            goodsImages.setGoodsId(goodsId);
            goodsImages.setImageUrl(domain+putRet.key);
            return goodsImageService.saveGoodsImages(goodsImages);
        } catch (Exception ex) {
            ex.printStackTrace();
           return BaseResult.error("文件上传失败!");
        }
    }


    /**
     * 多条件分页列表查询
     * @param goodsQuery
     * @return
     */
    @RequestMapping("queryGoodsForListPage")
    @ResponseBody
    public BaseResult queryGoodsForListPage(GoodsQuery goodsQuery){
        return goodsService.queryGoodsForListPage(goodsQuery);
    }

}
