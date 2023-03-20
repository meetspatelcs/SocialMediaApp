import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import ProfileAllImg from '../../../Cards/MyPosts/ProfileAllImg';

import '../../../Cards/MyPosts/myPosts.css';
import { Button } from 'react-bootstrap';

import './media.css';

const ProfileAllImages = () => {
    const {state} = useLocation();
    const {userId} = useParams();
    const navigate = useNavigate();

    const [imgList, setImgList] = useState([]);

    useEffect(() => {
        if(state){
            setImgList(state.myImgList);
        }
    }, [state]);

    function displayImg(){
        if(imgList != ''){
            return imgList.map((eachImg) => {
                return <ProfileAllImg key={eachImg.id} currImg={eachImg}/>   
            })
        }
    }

    function handleClick(){
        navigate(-1);
    }

    return (
        <div>
            <div className='profileAllImg-img pAllImg-mainBox justify-content-center align-items-center'>
                {displayImg()}
            </div>
            <div className='pAllImg-Backbox'>
                <Button onClick={handleClick}>Back To Profile</Button>
            </div>
        </div>
    );
};

export default ProfileAllImages;