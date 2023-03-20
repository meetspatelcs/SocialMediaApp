import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import OtherAllImg from '../../Cards/Page/OtherAllImg';
import { useUser } from '../../UserProvider/UserProvider';

import "./other.css"

const PageAllImages = () => {
    const {state} = useLocation();
    const [imgList, setImgList] = useState([]);
    
    useEffect(() => {
        if(state){
            setImgList(state.imgList);
        }
    }, [state])

    function displayImg(){
        if(imgList != ''){
          return imgList.map((eachImg) => {
            return <OtherAllImg key={eachImg.id} currImg = {eachImg}/>
          })
        }
    }

    return (
        <div className='pageAllImg-Img justify-content-center mt-3'>
            {displayImg()}
        </div>
    );
};

export default PageAllImages;