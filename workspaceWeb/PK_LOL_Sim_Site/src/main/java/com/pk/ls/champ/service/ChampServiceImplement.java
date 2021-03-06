 package com.pk.ls.champ.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.pk.ls.champ.dao.ChampDao;
import com.pk.ls.champ.vo.ChampVo;
import com.pk.ls.util.FileUtils;

import sun.util.logging.resources.logging;

@Service
public class ChampServiceImplement implements ChampService{

	@Autowired
	public ChampDao champDao;
	
	@Resource(name = "fileUtils")
	private FileUtils fileUtils;

	// 챔피언 포지션 별로 보는 거
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public  Map<String, Object> champList(String position, int start, int end) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<ChampVo> champList = champDao.champList(position, start, end);
		
		List<Map<String, Object>> fileList = new ArrayList<Map<String,Object>>();
		
		for (int i = 0; i < champList.size(); i++) {
			fileList = champDao.fileSelectList(champList.get(i).getChampionNumber());
		}
		
		resultMap.put("champList", champList);
		resultMap.put("fileList", fileList);
		
		return resultMap;
	}
	
	// 챔피언 리스트 페이징할 때 총 챔피언의 수를 구한다
	@Override
	public int champPositionTotalCount(String position) {

		return champDao.champPositionTotalCount(position);
	}
	
	// 챔피언 하나 상세 보기
	@Override
	public Map<String, Object> champSelectOne(int championNumber) {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<String, Object>();

		ChampVo champVo = (ChampVo) champDao.champSelectOne(championNumber);
		resultMap.put("champVo", champVo);

		List<Map<String, Object>> fileList = champDao.fileSelectList(championNumber);
		resultMap.put("fileList", fileList);

		return resultMap;
	}
	
	// 챔피언 등록
	@Transactional
	@Override
	public void champCreate(ChampVo champVo, MultipartHttpServletRequest 
			multipartHttpServletRequest) throws Exception{
		
		champDao.champCreate(champVo);
		
		int championNumber = champVo.getChampionNumber();
		
		List<Map<String, Object>> list = fileUtils.parseInsertFileInfo
				(championNumber, multipartHttpServletRequest);
		
		for (int i = 0; i < list.size(); i++) {
			champDao.insertFile(list.get(i));
		}
	}
	
	// 챔피언 정보 업데이트
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int champUpdateOne(ChampVo champVo, MultipartHttpServletRequest 
			multipartHttpServletRequest, int file_Index) 
			throws Exception {
	int resultNum = 0;
		
		try {
			resultNum = champDao.champUpdateOne(champVo);

			int ChampionNumber = champVo.getChampionNumber();
			Map<String, Object> tempFileMap = 
					champDao.fileSelectStoredFileName(ChampionNumber);

			List<Map<String, Object>> list = 
					fileUtils.parseInsertFileInfo(ChampionNumber, multipartHttpServletRequest);
			
			// 오로지 하나만 관리
			if (list.isEmpty() == false) {
				if(tempFileMap != null) {
					fileUtils.parseUpdateFileInfo(tempFileMap);
					champDao.fileDelete(ChampionNumber);
				}
				
				for (Map<String, Object> map : list) {
					champDao.insertFile(map);
				}

			}else if(file_Index == -1) {
				if(tempFileMap != null) {
					champDao.fileDelete(ChampionNumber);
					fileUtils.parseUpdateFileInfo(tempFileMap);
				}
			}
			
		}catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
			
		return resultNum;
	}

	// 챔피언 삭제
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int champDelete(int championNumber) throws Exception {

		Map<String, Object> tempFileMap = null;
		tempFileMap = champDao.fileSelectStoredFileName(championNumber);
		
		int result = 0;
		if(tempFileMap != null) {
			 result = champDao.fileDelete(championNumber);	
		}
		
		if(result != 0) {
			fileUtils.parseUpdateFileInfo(tempFileMap);
		}
		
		return champDao.champDelete(championNumber);
	}


}
