package com.sx.sxblog.controller;


import com.alibaba.fastjson.JSONObject;
import com.sx.sxblog.common.ReturnEntity;
import com.sx.sxblog.common.UserUtil;
import com.sx.sxblog.entity.Collect;
import com.sx.sxblog.entity.Tag;
import com.sx.sxblog.entity.User;
import com.sx.sxblog.service.impl.CollectServiceImpl;
import com.sx.sxblog.service.impl.TagServiceImpl;
import com.sx.sxblog.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lgx
 * @since 2020-07-08
 */
@RestController
public class CollectController {
    @Resource
    private CollectServiceImpl collectService;

    @Resource
    private TagServiceImpl tagService;

    @RequestMapping(value = "/getCollectList",method = RequestMethod.GET)
    @ResponseBody
    public ReturnEntity getCollectList(){
        JSONObject data = new JSONObject();
        List<Collect> collectList = collectService.getCollectList();
        data.put("collectList",collectList);
        return ReturnEntity.successResult(data);
    }

    @RequestMapping(value = "/getCollectById",method = RequestMethod.GET)
    @ResponseBody
    public ReturnEntity getCollectById(int collect_id){
        JSONObject data = new JSONObject();
        Collect collect = collectService.getCollectById(collect_id);
        data.put("collect_id",collect);
        return ReturnEntity.successResult(data);
    }

    @RequestMapping(value = "/insertCollect",method = RequestMethod.POST)
    @ResponseBody
    public ReturnEntity insertCollect(@RequestBody Collect collect){
        JSONObject data = new JSONObject();
        int result = collectService.insertCollect(collect);
        data.put("result",result);
        System.out.println(data);
        return ReturnEntity.successResult(data);
    }

    @RequestMapping(value = "/deleteCollect",method = RequestMethod.POST)
    @ResponseBody
    public ReturnEntity deleteCollect(@RequestBody Collect collect){
        JSONObject data = new JSONObject();
        int result = collectService.deleteCollect(collect.getCollectId());
        data.put("result",result);
        return ReturnEntity.successResult(data);
    }

    @GetMapping("/pub/collect_tags")
    @ResponseBody
    public ReturnEntity getAllTagOfCollect(@RequestBody Map<String,Object> user_post){
        JSONObject data = new JSONObject();
        String msg = "";
        boolean status = true;

        String userid = (String)user_post.get("userid");
        int id = UserUtil.switchToint(userid);
        List<Collect> collects= collectService.getCollectListByUserid(id);
        Set<String> tagSet = new HashSet<String>();

        //过程中使用list逐个添加
        List<Tag> tagList;
        int blogid;

        for (Collect collect: collects) {
            blogid = collect.getBlogId();
            tagList =  tagService.getTagsByBlogId(blogid);
            for (Tag tag : tagList){
                tagSet.add(tag.getTagName());
            }
        }
        data.put("tags",tagSet);
        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);
        return returnEntity;
    }

}
