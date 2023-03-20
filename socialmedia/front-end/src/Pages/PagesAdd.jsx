import React, { useEffect, useState } from 'react';
import PageNavbar from '../NavBar/PageNavbar';
import NavBar from '../NavBar/NavBar';
import { Button } from 'react-bootstrap';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';
import UserNoFollowedList from '../Cards/Page/UserNoFollowedList';

const PagesAdd = () => {
    const user = useUser();
    
    const [currWord, setCurrWord] = useState('');
    const [pageList, setPageList] = useState([]);
    const [searchList, setSearchList] = useState([]);
    const [isSearch, setIsSearch] = useState(false);

    function sendFetchRequestForPageList(){
        requestToPath(`/api/pages/search?page=${currWord}`, "GET", user.jwt)
            .then((searchResponse) => {
                setSearchList(searchResponse);
                setIsSearch(true);
            })
            .catch((error) => { console.log(error); })
    }

    useEffect(() => {
        requestToPath(`/api/pages/newPageList`, "GET", user.jwt)
            .then((listResponse) => { setPageList(listResponse); })
            .catch((error) => { console.log(error); })
    }, [])

    function handleClick(){
        sendFetchRequestForPageList();
    }

    function displaySearchBar(){
        return <input type='text' placeholder='page name' onChange={(e) => {setCurrWord(e.target.value)}} value={currWord} />
    }

    function displaySearchBarBtn(){
        return <Button onClick={handleClick}>Find Page</Button>
    }

    function displayClearBtn(){
        return <Button variant='danger' hidden={!isSearch} onClick={handleClear}>Clear search</Button>
    }

    function handleClear(){
        setIsSearch(false);
        setCurrWord("");
    }

    function displayContent(){
        if(searchList != '' && isSearch){
            return searchList.map((item) => {
                return <UserNoFollowedList key={item.id} pageName={item.compName} pageId={item.id} pageDesc={item.compDesc} />
            })
        }
        else if(pageList != ''){
            return pageList.map((eachPage) => {
                return <UserNoFollowedList key={eachPage.id} pageName={eachPage.compName} pageId={eachPage.id} pageDesc={eachPage.compDesc} />
            })
        }
    }

    return (
        <div>
            <div className='custom-main'>
            <PageNavbar />
                <div className='d-flex justify-content-center mt-5'>
                    {displaySearchBar()}{displaySearchBarBtn()}
                    {displayClearBtn()}
                </div>

                <div style={{border: "1px solid lightgrey", margin: "2rem 0.5rem", height: "75vh", overflowY:"auto"}}>
                    <div className='mt-3'>
                        {displayContent()}
                    </div>
                </div>
            </div>
            <NavBar />
        </div>
    );
};

export default PagesAdd;