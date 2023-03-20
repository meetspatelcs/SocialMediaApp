import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import ViewOtherMedia from '../../Cards/ViewOtherMedia';
import ViewOtherMediaV from '../../Cards/ViewOtherMediaV';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';
import '../Pages/other.css';

const OtherTab = (props) => {
    const user = useUser();
    const {otherTabVal} = props;
    const {userId, pageId} = useParams();
    const [imgList, setImgList] = useState([]);
    const [vidList, setVidList] = useState([]);

    const navigate = useNavigate();
   
    const imgType = 'image';
    const isImg = 'images';
    const isVid = 'videos';
    const videoType = 'video';

    useEffect(() => {
        // /api/pages/${pageId}/allMediaInfo/${imgType}
        requestToPath(`/api/pagePosts/page/${pageId}/allMediaInfo/${imgType}`, "GET", user.jwt)
            .then((ImgInfoResponse) => { setImgList(ImgInfoResponse); })
            .catch((error) => {console.log(error);})
    }, []);

    useEffect(() => {
        // /api/pages/${pageId}/allMediaInfo/${videoType}
        requestToPath(`/api/pagePosts/page/${pageId}/allMediaInfo/${videoType}`, "GET", user.jwt)
        .then((VideoInfoResponse) => { setVidList(VideoInfoResponse); })
        .catch((error) => {console.log(error);})
    }, [])

    function getMediaContent(){
        //centers the img 
        if(otherTabVal == isImg){
            return imgList.map((eachImg) => {
                return <div key={eachImg.id} className='otherTab-ImgVidMid' ><ViewOtherMedia currImg = {eachImg}/></div>
            })
        }
        else if(otherTabVal == isVid){
            return vidList.map((eachVid) => {
                return <div key={eachVid.id} className='otherTab-ImgVidMid' ><ViewOtherMediaV currVid={eachVid} /></div>;
            })
        }
    }

    function handleAllMediaButton(){
        if(otherTabVal == isImg){
            return <Button className='otherTab-allBtn' onClick={() => {navigate(`/users/${userId}/pages/page/${pageId}/profile/allImg`, {state: {imgList}})}}>All</Button>
        }
        else if(otherTabVal == isVid){
            return <Button className='otherTab-allBtn mt-4 mb-3' onClick={() => {navigate(`/users/${userId}/pages/page/${pageId}/profile/allVid`, {state: {vidList}})}}>All</Button>
        }
    }

    return (
        <div style={{overflow:"hidden", maxHeight: "22vh"}}>
            <div className='d-flex justify-content-sm-end p-2'>
                {/* <Button className='w-100 ms-2 me-2' >All</Button> */}
                {handleAllMediaButton()}
            </div>
            
            <div className='page-others justify-content-center align-items-center'>
                {getMediaContent()}
            </div>  
        </div>
    );
};

export default OtherTab;