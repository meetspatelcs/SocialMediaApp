import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import {LazyLoadImage} from "react-lazy-load-image-component" ;
import NavBar from '../NavBar/NavBar';
import { useUser } from '../UserProvider/UserProvider';
import requestToPath from '../Service/fetchService';
import ViewPostsComp from '../Cards/ViewPostsComp';
import CreatePostModal from '../Modals/CreatePostModal';
import AboutMain from '../Components/MyPosts/About/AboutMain';
import FollowMain from '../Components/MyPosts/Follow/FollowMain';
import FriendsMain from '../Components/MyPosts/Friends/FriendsMain';
import MediaMain from '../Components/MyPosts/Media/MediaMain';


import '../Components/MyPosts/userProfile.css';
import ThumbnailModal from '../Modals/MyPosts/Thumbnail/ThumbnailModal';

const MyPosts = () => {
    const user = useUser();
    const {userId} = useParams();
    
    const[myPosts, setMyPosts] = useState([]);
    const[postShow, setPostShow] = useState(false);
    const[thumbnailShow, setThumbnailShow] = useState(false);
    const[myurl, setMyurl] = useState(null);
  
    function handlePostClose(){setPostShow(false);}
    function handlePostShow(){setPostShow(true);}
    function handleThumbnailsClose(){setThumbnailShow(false)};
    function handleThumbnailsShow(){setThumbnailShow(true)};

    useEffect(() => {
        requestToPath(`/api/posts/myPosts`, "GET", user.jwt)
            .then((myPostsData) => { setMyPosts(myPostsData); })
            .catch((error) => {console.log(error);})
    }, [])

    useEffect(() => {
        fetch(`/api/userThumbnails`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
            .then((thumbResponse) => { return thumbResponse.blob(); })
            .then((data) => { setMyurl(URL.createObjectURL(data)); return data; })
            .catch((error) => {console.log(error);})
    }, [])

    function removeThumbRequest(){
        requestToPath(`/api/userThumbnails/remove`, "PUT", user.jwt)
            .then((removeResponse) => { setMyurl(null); })
            .catch((error) => {console.log(error);})
    }

    function displayPosts(){
        if(myPosts != null){
            return myPosts.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachPost) => {
                    return <div key={eachPost.id}><ViewPostsComp eachMyPost={eachPost} /></div>
                })
        }       
        return <div>No posts to Show!</div>
    }

    function thumbnailUpload(){
        return <div className='upload-btn d-flex justify-content-between'>
                    <Button onClick={handleThumbnailsShow}>Upload</Button>
                    <Button variant='secondary' onClick={() => {removeThumbRequest();}}> Remove </Button>
               </div>
    }

    return (
        <div style={{margin:"0", padding:"0"}}>
            <div className='custom-main'>                
                <div className='userThumb-styl' >
                    <LazyLoadImage thumbnail="true" src={myurl}  />
                </div>
                
                {thumbnailUpload()}
                <ThumbnailModal show={thumbnailShow} emitThumbnailClose={handleThumbnailsClose} />
                
                <div className='custom-profileGrid justify-content-center'>
                    <AboutMain />

                    <MediaMain />

                    <FollowMain />

                    <FriendsMain />
                </div>

                <div>
                    <div style={{display:"flex", justifyContent:"center", alignItems:"center", height:"200px"}}>
                        <Button className='custom-postBtn' onClick={() => {handlePostShow();}}>Create Post</Button>
                        <CreatePostModal show={postShow} emitHandleClose={handlePostClose}/>
                    </div>
                    {displayPosts()}
                </div>

                <div className='text-center mt-2' style={{marginBottom: "7rem"}}>
                    Change this to something
                </div>
            </div>
            <NavBar />
        </div>
    );
};

export default MyPosts;