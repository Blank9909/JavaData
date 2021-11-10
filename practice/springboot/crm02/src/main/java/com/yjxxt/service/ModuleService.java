package com.yjxxt.service;

import com.yjxxt.base.BaseService;
import com.yjxxt.bean.Module;
import com.yjxxt.dto.TreeDto;
import com.yjxxt.mapper.ModuleMapper;
import com.yjxxt.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ModuleService extends BaseService<Module,Integer>{


    @Resource
    private ModuleMapper moduleMapper;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    public List<TreeDto> findModules(){
        return moduleMapper.selectModules();
    }

    public List<TreeDto> findModules2(Integer roleId){
        //查询所欲的资源信息
        List<TreeDto> tlist=findModules();
        //查询当前角色所拥有资源id的集合
        List<Integer> roleHasMid = permissionMapper.selectRoleHasAllModuleIds(roleId);
        //遍历
        //判断当前角色是否拥有资源id
        if(roleHasMid!=null&&roleHasMid.size()>0){
            //遍历所有的资源
            for(TreeDto treeDto:tlist){
                //当前角色用户资源mid，是否在全体资源中出现过
                if(roleHasMid.contains(treeDto.getId())){
                    treeDto.setChecked(true);
                }
            }
        }
        //返回目标集合对象
        return tlist;
    }

}