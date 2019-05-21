package com.example.esdemo.web;

import com.example.esdemo.dao.CategoryDao;
import com.example.esdemo.pojo.Category;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class CategoryController {

    @Autowired
    private CategoryDao categoryDao;

    //测试首页

    @GetMapping("")
    public String index(){
        return "index";
    }

    //每页数量
    @RequestMapping("list")
    public String list(){
        return "listCategory";
    }


    @RequestMapping("/listCategory")
    public String listCategory(Model m, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) {

//        System.out.println(1);
//        String query = "商品";  //查询条件, 但是并未使用,放在这里,为的是将来使用, 方便参考, 知道如何用
//        System.out.println(2);
//        SearchQuery searchQuery = getEntitySearchQuery(start, size, query);
//        System.out.println(3);
//        Page<Category> page = categoryDao.search(searchQuery);
//        System.out.println(4);
//        m.addAttribute("page",page);
//        return "listCategory";

        String query = "商品"; //查询条件，但是并未使用，放在这里，为的是将来使用，方便参考，知道如何用
        SearchQuery searchQuery=getEntitySearchQuery(start,size,query);
        Page<Category> page = categoryDao.search(searchQuery);
        m.addAttribute("page", page);
        return "listCategory";
    }

    private SearchQuery getEntitySearchQuery(int start, int size, String searchContent) {
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchAllQuery(),  //查询所有
                ScoreFunctionBuilders.weightFactorFunction(100))
                //查询条件,到那时并未使用,放在这里,为的是将来使用,方便参考,知道如何用
//                .add(QueryBuilders.matchPhraseQuery("name",searchContent),ScoreFunctionBuilders.weightFactorFunction(100))
                //设置权重分 求和模式
                .scoreMode("sum")
                //设置权重分最低分
                .setMinScore(10);

        //设置分页,一开始没数据,es有bug,会报异常"No mapping found for [id] in order to sort on"
        //...所以得注释掉, 等有了数据再注释回来即可
        //按理说应该有无论有无数据都可以解决的办法, 目前还没想到, 就先这样.
//        Sort sort = new Sort(Sort.Direction.DESC,"id");
//        Pageable pageable = new PageRequest(start,size,sort);
        Pageable pageable = new PageRequest(start,size);
        return new NativeSearchQueryBuilder().withPageable(pageable).withQuery(functionScoreQueryBuilder).build();
    }

    @RequestMapping("/addCategory")
    public String addCategory(Category c)throws  Exception{
        int id = currentTime();
        c.setId(id);
        categoryDao.save(c);
        return "redirect:listCategory";
    }

    private int currentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String time = sdf.format(new Date());
        return Integer.parseInt(time);
    }

    @RequestMapping("/deleteCategory")
    public String deleteCategory(Category c) throws Exception{
        categoryDao.delete(c);
        return "redirect:listCategory";
    }

    @RequestMapping("/updateCategory")
    public String updateCategory(Category c) throws Exception{
        categoryDao.save(c);
        return "redirect:listCategory";
    }

    @RequestMapping("/editCategory")
    public String editCategory(int id,Model m) throws Exception{
        Category c = categoryDao.findOne(id);
        m.addAttribute("c",c);
        return "editCategory";
    }


}
