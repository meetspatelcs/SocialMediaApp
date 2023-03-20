import React, { useEffect, useState } from 'react';
import { Button, Image } from 'react-bootstrap';
import { useUser } from '../../UserProvider/UserProvider';
import { useParams } from 'react-router-dom';
import ThumbnailAbout from '../../Cards/ThumbnailAbout';
import VIewPagePostCard from '../../Cards/VIewPagePostCard';
import CreatePagePostModal from '../../Modals/CreatePagePostModal';
import PageThumbnailModal from '../../Modals/PageThumbnailModal';
import requestToPath from '../../Service/fetchService';

import './other.css';

const PageMain = (props) => {
  const user = useUser();
  const {pageId} = useParams();
  const {userRole} = props;

  const[pageInfo, setPageInfo] = useState({});
  const[thumbRef, setThumbRef] = useState();
  const[pagePosts, setPagePosts] = useState([]);
  const[show, setShow] = useState(false);
  const[postShow, setPostShow] = useState(false);
  const[followers, setFollowers] = useState(0);

  function handleShow(){setShow(true);}
  function handleClose(){setShow(false);}
  function handlePostShow(){setPostShow(true);}
  function handlePostClose(){setPostShow(false);}

  useEffect(() => {
    requestToPath(`/api/pages/${pageId}/pageInfo`, "GET", user.jwt)
      .then((pageResponse) => { setPageInfo(pageResponse); })
      .catch((error) => {console.log(error);})
  },[])

  // /api/pages/${pageId}/thumbnails
  useEffect(() => {
    fetch(`/api/pageThumbnails/page/${pageId}/thumbnails`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
      .then((thumbnailResponse) => { return thumbnailResponse.blob(); })
      .then((data) => {
      setThumbRef(URL.createObjectURL(data))
      return data;
      })
      .catch((error) => { console.log(error); })
  },[])

  // /api/pages/${pageId}/allPosts
  useEffect(() => {
    requestToPath(`/api/pagePosts/page/${pageId}/allPosts`, "GET", user.jwt)
      .then((pagePostResponse) => { setPagePosts(pagePostResponse); })
      .catch((error) => { console.log(error); })
  },[])

  useEffect(() => {
    requestToPath(`/api/pages/${pageId}/followCount`, "GET", user.jwt)
      .then((followCountResponse) => { setFollowers(followCountResponse); })
      .catch((error) => { console.log(error); })
  },[])

  function displayPosts(){
    if(pagePosts != null){
      return pagePosts.sort((a,b) => b.id > a.id ? 1 : -1)
        .map((eachPost) => {
          return <div key={eachPost.id}><VIewPagePostCard post={eachPost} /></div>
      })
    }
    return <div>No posts to Show!</div>
  }

  // /api/pages/${pageId}/thumbnails/remove
  function removeThumbRequest(){
    requestToPath(`/api/pageThumbnails/page/${pageId}/thumbnails/remove`, "PUT", user.jwt)
      .then((removeResponse) => { console.log(removeResponse); window.location.reload();})
      .catch((error) => {console.log(error);})
  }

  return (
    <div className='custom-main'>
      <div className='custom-thumbnailContainer pb-1'> 
        <div className='pageThumb-img'>
        {thumbRef ? <div><Image thumbnail="true" src={thumbRef}  /></div> : <div></div> }
        </div>

        <div className='d-flex justify-content-sm-around'>
          <Button className='mt-1 ms-1 mb-5' onClick={handleShow}>Upload thumbnail</Button>
          <Button className='mt-1 ms-1 mb-5' variant='secondary' onClick={() => {removeThumbRequest();}}>Remove</Button>
          <Button className='mt-1 ms-1 mb-5' variant=''>{"Followers: " + followers}</Button>
        </div>

        <div>
          <ThumbnailAbout 
          pageName={pageInfo.compName} 
          pageDesc={pageInfo.compDesc}
          pageEmail={pageInfo.contactEmail}
          pagePhone={pageInfo.contactPhone}/>
        </div>
      </div>

      <div>
        <div style={{display:"flex", justifyContent:"center", alignItems:"center", height:"200px"}}>
        <Button className='custom-postBtn' onClick={handlePostShow}>Create Post</Button>
        </div>
        {displayPosts()}
        <div className='text-center mb-5'>
        No Posts to see
        </div>
      </div>
    <CreatePagePostModal postShow = {postShow} emitHandlePostClose={handlePostClose} />
    <PageThumbnailModal show={show} emitHandleClose={handleClose} />
    </div>
  );
};

export default PageMain;