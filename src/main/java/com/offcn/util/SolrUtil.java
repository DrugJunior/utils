package com.offcn.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import com.offcn.pojo.TbItemExample.Criteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring-*.xml")
public class SolrUtil {
	
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Test
	public void importData(){
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> list = itemMapper.selectByExample(example);
		
		for(TbItem item : list){
			//
			Map<String, String> spec= JSON.parseObject(item.getSpec(),Map.class);
			
			Map map = new HashMap();
			
			for(Map.Entry<String, String> entry : spec.entrySet()){
				
				map.put(Pinyin.toPinyin(entry.getKey(), "").toLowerCase(), entry.getValue());  
			}
			
			/*
			for(String key : spec.keySet()) {
				map.put(Pinyin.toPinyin(key, "").toLowerCase(), spec.get(key));
			}*/
			item.setSpecMap(map);
			System.out.println(item.getTitle()+"<-------->"+item.getPrice());
		}
		solrTemplate.saveBeans("core1",list);
		solrTemplate.commit("core1");
		System.out.println("------------导入成功------------");
	}
	
	//删除全部数据
		@Test
		public void deleteAll(){
			Query query=new SimpleQuery("*:*");
			solrTemplate.delete("core1",query);
			solrTemplate.commit("core1");
			System.out.println("删除成功");
		}
	
	
}
