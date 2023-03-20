import React, { useEffect, useState } from 'react';
import PageNavBar from '../NavBar/PageNavbar'
import NavBar from '../NavBar/NavBar'
import { useUser } from '../UserProvider/UserProvider';
import requestToPath from '../Service/fetchService';
import UserFollowPageList from '../Cards/UserFollowPageList';
const Pages = () => {
    const user = useUser();
    const [pagesInfo, setPagesInfo] = useState([]);

    useEffect(() => {
        requestToPath(`/api/pages/followingPages`, "GET", user.jwt)
            .then((followResponse) => { setPagesInfo(followResponse); })
            .catch((error) => {console.log(error);})
    }, [])

    function displayPageList(){
        if(pagesInfo != ''){
            return  pagesInfo.map((eachPage) => {
                return  <UserFollowPageList key={eachPage.id} 
                pageId={eachPage.id} 
                pageName={eachPage.compName} 
                pageDesc={eachPage.compDesc} />
            })
        }
        return <div>No pages to show!</div>
    }

    return (
        <div>
            <div className='custom-main'>
                <PageNavBar />

                <div className='mt-5' style={{marginBottom: "10vh"}}>
                  {displayPageList()}
                  <div className='d-flex justify-content-center'>
                        No More
                    </div>
                </div>
            </div>
            <NavBar />
        </div>
    );
};

export default Pages;