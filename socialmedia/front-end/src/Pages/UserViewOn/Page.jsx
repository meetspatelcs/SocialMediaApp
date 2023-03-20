import React, { useEffect, useState } from 'react';
import { Button, Image } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import ThumbnailAbout from '../../Cards/ThumbnailAbout';
import UserViewPagePost from '../../Cards/UserViewPagePost';
import NavBar from '../../NavBar/NavBar';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';

const Page = (props) => {
  const user = useUser();
  const {pageId} = useParams();
  const {userRole} = props;

  const [pageInfo, setPageInfo] = useState({});
  const [pagePosts, setPagePosts] = useState([]);
  const [thumbRef, setThumbRef] = useState();
  const [followers, setFollowers] = useState(0);

  // /api/pages/${pageId}/thumbnails
  useEffect(() => {
    fetch(`/api/pageThumbnails/page/${pageId}/thumbnails`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
      .then((thumbnailResponse) => { return thumbnailResponse.blob(); })
      .then((data) => { setThumbRef(URL.createObjectURL(data)); return data; })
      .catch((error) => {console.log(error);})
  }, []) 

  useEffect(() => {
    requestToPath(`/api/pages/${pageId}/pageInfo`, "GET", user.jwt)
      .then((pageResponse) => { setPageInfo(pageResponse); })
      .catch((error) => {console.log(error);})
  }, [])

  // /api/pages/${pageId}/allPosts
  useEffect(() => {
    requestToPath(`/api/pagePosts/page/${pageId}/allPosts`, "GET", user.jwt)
      .then((pagePostResponse) => { setPagePosts(pagePostResponse); })
      .catch((error) => {console.log(error);})
  }, [])

  useEffect(() => {
    requestToPath(`/api/pages/${pageId}/followCount`, "GET", user.jwt)
      .then((followCountResponse) => { setFollowers(followCountResponse); })
      .catch((error) => { console.log(error); })
  }, [])

  function followPageRequest(){
    requestToPath(`/api/pages/${pageId}/join`, "POST", user.jwt)
      .then((joinResponse) => { window.location.reload(); })
      .catch((error) => {console.log(error);})
  }

  function unfollowPageRequest(){
    requestToPath(`/api/pages/${pageId}/unfollow`, "DELETE", user.jwt)
      .then((unfollowResponse) => { window.location.reload(); })
      .catch((error) => {window.location.reload();})
  }

  function isRole(){
    if(userRole == 'ROLE_USER'){ return  <Button className='ms-1 mb-5' 
    onClick={() => {unfollowPageRequest()}} variant='secondary'>unfollow</Button> }
    return <Button className='ms-1 mb-5' onClick={() => {followPageRequest()}} >Follow</Button>  
  }

  function displayPosts(){
    if(pagePosts !== null){
      return pagePosts.sort((a,b) => b.id > a.id ? 1 : -1)
        .map((eachPost) => {
          return <div key={eachPost.id}> <UserViewPagePost post={eachPost} /> </div>;
        })
      }
    return <div> No Posts to Show!</div>
  }

  return (
    <div>
      <div className='custom-main'>

      <div className='custom-thumbnailContainer pb-1'> 
        
        <div style={{display: "flex", justifyContent:"center",alignItems:"center", width:"100%", height:"15rem", overflow: "hidden"}}>
          <Image thumbnail src={thumbRef}  />
        </div>

        <div className='d-flex justify-content-sm-around'>
          {isRole()}
          {"Followers: " + followers} 
        </div>

        <div><ThumbnailAbout 
              pageName={pageInfo.compName} 
              pageDesc={pageInfo.compDesc}
              pageEmail={pageInfo.contactEmail}
              pagePhone={pageInfo.contactPhone}    
              />  
          </div>
      </div>

      <div>{displayPosts()}

        <div className='text-center mb-5'>
            No Posts to see
        </div>
      </div>
      </div>
      <NavBar />
    </div>
  );
};

export default Page;