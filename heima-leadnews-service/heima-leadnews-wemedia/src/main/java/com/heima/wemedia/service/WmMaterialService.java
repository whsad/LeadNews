package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    ResponseResult upload_picture(MultipartFile multipartFile);

    /**
     * 查看图片
     * @param dto
     * @return
     */
    ResponseResult search_list(WmMaterialDto dto);

    /**
     * 删除图片
     * @param id
     * @return
     */
    ResponseResult deleteById(Integer id);

    /**
     * 图片收藏与取消
     * @param id
     * @return
     */
    ResponseResult collectOrCancelCollect(Integer id);
}
