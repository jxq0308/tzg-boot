package org.jeecg.modules.tao.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.modules.tao.entity.TzgCustomer;
import org.jeecg.modules.tao.entity.TzgCustomerCashAccount;
import org.jeecg.modules.tao.entity.TzgCustomerContact;
import org.jeecg.modules.tao.entity.TzgCustomerShop;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.tao.vo.TzgCustomerPage;
import org.jeecg.modules.tao.service.ITzgCustomerService;
import org.jeecg.modules.tao.service.ITzgCustomerShopService;
import org.jeecg.modules.tao.service.ITzgCustomerContactService;
import org.jeecg.modules.tao.service.ITzgCustomerCashAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;

 /**
 * @Title: Controller
 * @Description: 客户信息
 * @author： jeecg-boot
 * @date：   2019-04-16
 * @version： V1.0
 */
@RestController
@RequestMapping("/tao/tzgCustomer")
@Slf4j
public class TzgCustomerController {
	@Autowired
	private ITzgCustomerService tzgCustomerService;
	@Autowired
	private ITzgCustomerShopService tzgCustomerShopService;
	@Autowired
	private ITzgCustomerContactService tzgCustomerContactService;
	@Autowired
	private ITzgCustomerCashAccountService tzgCustomerCashAccountService;
	
	/**
	  * 分页列表查询
	 * @param tzgCustomer
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<TzgCustomer>> queryPageList(TzgCustomer tzgCustomer,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                    HttpServletRequest req) {
		Result<IPage<TzgCustomer>> result = new Result<IPage<TzgCustomer>>();
		QueryWrapper<TzgCustomer> queryWrapper = QueryGenerator.initQueryWrapper(tzgCustomer, req.getParameterMap());
		Page<TzgCustomer> page = new Page<TzgCustomer>(pageNo, pageSize);
		IPage<TzgCustomer> pageList = tzgCustomerService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param tzgCustomerPage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<TzgCustomer> add(@RequestBody TzgCustomerPage tzgCustomerPage) {
		Result<TzgCustomer> result = new Result<TzgCustomer>();
		try {
			TzgCustomer tzgCustomer = new TzgCustomer();
			BeanUtils.copyProperties(tzgCustomerPage, tzgCustomer);
			
			tzgCustomerService.save(tzgCustomer);
			result.success("添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param tzgCustomerPage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<TzgCustomer> edit(@RequestBody TzgCustomerPage tzgCustomerPage) {
		Result<TzgCustomer> result = new Result<TzgCustomer>();
		TzgCustomer tzgCustomer = new TzgCustomer();
		BeanUtils.copyProperties(tzgCustomerPage, tzgCustomer);
		TzgCustomer tzgCustomerEntity = tzgCustomerService.getById(tzgCustomer.getId());
		if(tzgCustomerEntity ==null) {
			result.error500("未找到对应实体");
		}else {
			tzgCustomerService.updateById(tzgCustomer);
			result.success("修改成功!");
		}
		
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<TzgCustomer> delete(@RequestParam(name="id",required=true) String id) {
		Result<TzgCustomer> result = new Result<TzgCustomer>();
		TzgCustomer tzgCustomer = tzgCustomerService.getById(id);
		if(tzgCustomer ==null) {
			result.error500("未找到对应实体");
		}else {
			tzgCustomerService.delMain(id);
			result.success("删除成功!");
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<TzgCustomer> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<TzgCustomer> result = new Result<TzgCustomer>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.tzgCustomerService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<TzgCustomer> queryById(@RequestParam(name="id",required=true) String id) {
		Result<TzgCustomer> result = new Result<TzgCustomer>();
		TzgCustomer tzgCustomer = tzgCustomerService.getById(id);
		if(tzgCustomer ==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(tzgCustomer);
			result.setSuccess(true);
		}
		return result;
	}
	
	//===========================以下是子表信息操作相关API====================================
	
	/**
	  * 通过主表id查询店铺信息
	 * @param mainId
	 * @return
	 */
	@GetMapping(value = "/listTzgCustomerShopByMainId")
	public Result<List<TzgCustomerShop>> queryTzgCustomerShopListByMainId(@RequestParam(name="mainId",required=false) String mainId) {
		Result<List<TzgCustomerShop>> result = new Result<List<TzgCustomerShop>>();
		List<TzgCustomerShop> tzgCustomerShopList = null;
		if (mainId != null) {
			tzgCustomerShopList = tzgCustomerShopService.selectByMainId(mainId);
            result.setResult(tzgCustomerShopList);
            result.setSuccess(true);
            return result;
		}else return null;	
	}
	
	/**
     * 添加店铺信息
     *
     * @param tzgCustomerShop
     * @return
     */
    @PostMapping(value = "/addTzgCustomerShop")
    public Result<TzgCustomerShop> addTzgCustomerShop(@RequestBody TzgCustomerShop tzgCustomerShop) {
        Result<TzgCustomerShop> result = new Result<>();
        try {
            boolean ok = tzgCustomerShopService.save(tzgCustomerShop);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("添加店铺信息成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("添加店铺信息失败!");
            }
            return result;
        } catch (Exception e) {
            e.fillInStackTrace();
            result.setSuccess(false);
            result.setMessage("添加店铺信息过程中出现了异常: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 编辑店铺信息
     *
     * @param tzgCustomerShop
     * @return
     */
    @PutMapping("/editTzgCustomerShop")
    public Result<TzgCustomerShop> editTzgCustomerShop(@RequestBody TzgCustomerShop tzgCustomerShop) {
        Result<TzgCustomerShop> result = new Result<>();
        try {
            boolean ok = tzgCustomerShopService.updateById(tzgCustomerShop);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("更新店铺信息成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("更新店铺信息失败!");
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("更新数据过程中出现异常啦: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 通过id删除店铺信息
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deleteTzgCustomerShop")
    public Result<TzgCustomerShop> deleteTzgCustomerShop(@RequestParam(name = "id", required = true) String id) {
        Result<TzgCustomerShop> result = new Result<>();
        try {
            boolean ok = tzgCustomerShopService.removeById(id);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("删除店铺信息成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("删除店铺信息失败!");
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("删除店铺信息过程中出现异常啦: " + e.getMessage());
            return result;
        }
    }
	
	/**
     * 批量删除店铺信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatchTzgCustomerShop")
    public Result<TzgCustomerShop> deleteBatchTzgCustomerShop(@RequestParam(name = "ids", required = true) String ids) {
        Result<TzgCustomerShop> result = new Result<TzgCustomerShop>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.tzgCustomerShopService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }
    
	/**
	  * 通过主表id查询联系人
	 * @param mainId
	 * @return
	 */
	@GetMapping(value = "/listTzgCustomerContactByMainId")
	public Result<List<TzgCustomerContact>> queryTzgCustomerContactListByMainId(@RequestParam(name="mainId",required=false) String mainId) {
		Result<List<TzgCustomerContact>> result = new Result<List<TzgCustomerContact>>();
		List<TzgCustomerContact> tzgCustomerContactList = null;
		if (mainId != null) {
			tzgCustomerContactList = tzgCustomerContactService.selectByMainId(mainId);
            result.setResult(tzgCustomerContactList);
            result.setSuccess(true);
            return result;
		}else return null;	
	}
	
	/**
     * 添加联系人
     *
     * @param tzgCustomerContact
     * @return
     */
    @PostMapping(value = "/addTzgCustomerContact")
    public Result<TzgCustomerContact> addTzgCustomerContact(@RequestBody TzgCustomerContact tzgCustomerContact) {
        Result<TzgCustomerContact> result = new Result<>();
        try {
            boolean ok = tzgCustomerContactService.save(tzgCustomerContact);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("添加联系人成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("添加联系人失败!");
            }
            return result;
        } catch (Exception e) {
            e.fillInStackTrace();
            result.setSuccess(false);
            result.setMessage("添加联系人过程中出现了异常: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 编辑联系人
     *
     * @param tzgCustomerContact
     * @return
     */
    @PutMapping("/editTzgCustomerContact")
    public Result<TzgCustomerContact> editTzgCustomerContact(@RequestBody TzgCustomerContact tzgCustomerContact) {
        Result<TzgCustomerContact> result = new Result<>();
        try {
            boolean ok = tzgCustomerContactService.updateById(tzgCustomerContact);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("更新联系人成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("更新联系人失败!");
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("更新数据过程中出现异常啦: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 通过id删除联系人
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deleteTzgCustomerContact")
    public Result<TzgCustomerContact> deleteTzgCustomerContact(@RequestParam(name = "id", required = true) String id) {
        Result<TzgCustomerContact> result = new Result<>();
        try {
            boolean ok = tzgCustomerContactService.removeById(id);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("删除联系人成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("删除联系人失败!");
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("删除联系人过程中出现异常啦: " + e.getMessage());
            return result;
        }
    }
	
	/**
     * 批量删除联系人
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatchTzgCustomerContact")
    public Result<TzgCustomerContact> deleteBatchTzgCustomerContact(@RequestParam(name = "ids", required = true) String ids) {
        Result<TzgCustomerContact> result = new Result<TzgCustomerContact>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.tzgCustomerContactService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }
    
	/**
	  * 通过主表id查询银行卡
	 * @param mainId
	 * @return
	 */
	@GetMapping(value = "/listTzgCustomerCashAccountByMainId")
	public Result<List<TzgCustomerCashAccount>> queryTzgCustomerCashAccountListByMainId(@RequestParam(name="mainId",required=false) String mainId) {
		Result<List<TzgCustomerCashAccount>> result = new Result<List<TzgCustomerCashAccount>>();
		List<TzgCustomerCashAccount> tzgCustomerCashAccountList = null;
		if (mainId != null) {
			tzgCustomerCashAccountList = tzgCustomerCashAccountService.selectByMainId(mainId);
            result.setResult(tzgCustomerCashAccountList);
            result.setSuccess(true);
            return result;
		}else return null;	
	}
	
	/**
     * 添加银行卡
     *
     * @param tzgCustomerCashAccount
     * @return
     */
    @PostMapping(value = "/addTzgCustomerCashAccount")
    public Result<TzgCustomerCashAccount> addTzgCustomerCashAccount(@RequestBody TzgCustomerCashAccount tzgCustomerCashAccount) {
        Result<TzgCustomerCashAccount> result = new Result<>();
        try {
            boolean ok = tzgCustomerCashAccountService.save(tzgCustomerCashAccount);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("添加银行卡成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("添加银行卡失败!");
            }
            return result;
        } catch (Exception e) {
            e.fillInStackTrace();
            result.setSuccess(false);
            result.setMessage("添加银行卡过程中出现了异常: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 编辑银行卡
     *
     * @param tzgCustomerCashAccount
     * @return
     */
    @PutMapping("/editTzgCustomerCashAccount")
    public Result<TzgCustomerCashAccount> editTzgCustomerCashAccount(@RequestBody TzgCustomerCashAccount tzgCustomerCashAccount) {
        Result<TzgCustomerCashAccount> result = new Result<>();
        try {
            boolean ok = tzgCustomerCashAccountService.updateById(tzgCustomerCashAccount);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("更新银行卡成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("更新银行卡失败!");
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("更新数据过程中出现异常啦: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 通过id删除银行卡
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deleteTzgCustomerCashAccount")
    public Result<TzgCustomerCashAccount> deleteTzgCustomerCashAccount(@RequestParam(name = "id", required = true) String id) {
        Result<TzgCustomerCashAccount> result = new Result<>();
        try {
            boolean ok = tzgCustomerCashAccountService.removeById(id);
            if (ok) {
                result.setSuccess(true);
                result.setMessage("删除银行卡成功.");
            } else {
                result.setSuccess(false);
                result.setMessage("删除银行卡失败!");
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("删除银行卡过程中出现异常啦: " + e.getMessage());
            return result;
        }
    }
	
	/**
     * 批量删除银行卡
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatchTzgCustomerCashAccount")
    public Result<TzgCustomerCashAccount> deleteBatchTzgCustomerCashAccount(@RequestParam(name = "ids", required = true) String ids) {
        Result<TzgCustomerCashAccount> result = new Result<TzgCustomerCashAccount>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.tzgCustomerCashAccountService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }
    

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<TzgCustomer> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                TzgCustomer tzgCustomer = JSON.parseObject(deString, TzgCustomer.class);
                queryWrapper = QueryGenerator.initQueryWrapper(tzgCustomer, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<TzgCustomerPage> pageList = new ArrayList<TzgCustomerPage>();
        List<TzgCustomer> tzgCustomerList = tzgCustomerService.list(queryWrapper);
        for (TzgCustomer tzgCustomer : tzgCustomerList) {
            TzgCustomerPage vo = new TzgCustomerPage();
            BeanUtils.copyProperties(tzgCustomer, vo);
            List<TzgCustomerShop> tzgCustomerShopList = tzgCustomerShopService.selectByMainId(tzgCustomer.getId());
            vo.setTzgCustomerShopList(tzgCustomerShopList);
            List<TzgCustomerContact> tzgCustomerContactList = tzgCustomerContactService.selectByMainId(tzgCustomer.getId());
            vo.setTzgCustomerContactList(tzgCustomerContactList);
            List<TzgCustomerCashAccount> tzgCustomerCashAccountList = tzgCustomerCashAccountService.selectByMainId(tzgCustomer.getId());
            vo.setTzgCustomerCashAccountList(tzgCustomerCashAccountList);
            pageList.add(vo);
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "客户信息列表");
        mv.addObject(NormalExcelConstants.CLASS, TzgCustomerPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("客户信息列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<TzgCustomerPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TzgCustomerPage.class, params);
                for (TzgCustomerPage page : list) {
                    TzgCustomer po = new TzgCustomer();
                    BeanUtils.copyProperties(page, po);
                    tzgCustomerService.saveMain(po, page.getTzgCustomerShopList(),page.getTzgCustomerContactList(),page.getTzgCustomerCashAccountList());
                }
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage());
                return Result.error("文件导入失败！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }
}
