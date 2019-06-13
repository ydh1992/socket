package com.socket.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeMapper {

    String getKeyWord();

}