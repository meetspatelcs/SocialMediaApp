import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import ViewAllImg from '../../../Cards/MyPosts/UserViewOn/ViewAllImg';

import '../../../Cards/MyPosts/myPosts.css';
import { Button } from 'react-bootstrap';

const ViewProfileAllImages = () => {
    const {state} = useLocation();
    const {userId, visitUserId} = useParams();
    const navigate = useNavigate();

    const [imgList, setImgList] = useState([]);

    useEffect(() => {
        if(state){
            setImgList(state.userImgList);
        }
    }, [state])

    function displayImg(){
        if(imgList != ''){
            return imgList.map((eachImg) => {
                return <ViewAllImg currImg={eachImg}/>
            })
        }
    }

    function handleClick(){
        navigate(-1);    
    }

    return (
        <div>
            <div className='vProfileAllImg-img pAllImg-mainBox justify-content-center align-items-center'>
                {displayImg()}
            </div>
            <div className='pAllImg-Backbox'>
                <Button onClick={handleClick}>Back To Profile</Button>   
            </div>
        </div>
    );
};

export default ViewProfileAllImages;